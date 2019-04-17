rm(list = ls())

library("rjson")
library("purrr")
library("dplyr")
library("rlist")
library("grDevices")
library("zoo")
library("plotrix")

loadJson = function() {
  fromJSON(file = "E:/Backups/Steven-3rdYrProject/R analysis/All.txt")
}

data = loadJson()
intervention = data %>% keep( ~ .x$storyteller == "EDA")
control = data %>% keep( ~ .x$storyteller == "REPEAT")

mapcol = function(list, col) {
  list %>% map( ~ .x[[col]]) %>% return
}

# Convert loaded JSON data to zoos
toZoo = function(edaData) {
  times = edaData %>% names %>% as.numeric
  values = edaData %>% unname %>% as.numeric
  
  zoo(values, times) %>% return
}

getZooValue = function(zoo, time) {
  idx = which(index(zoo) == time)
  coredata(zoo)[[idx]] %>% return
}

# Remove all data with x that is not between minX and maxX (inclusive)
filterZooByIndex = function(zoo, minX, maxX) {
  keepX = index(zoo) %>% keep( ~ .x >= minX && .x <= maxX)
  empty = zoo(,keepX)
  merged = merge(zoo, empty, all=FALSE)
  return(merged)
}

# Get a list of all x values that appear in multiple zoos
getAllX = function(zoos) {
  newX = zoos %>%
    map( ~ .x %>% index) %>%
    flatten %>%
    return
}

# Linearly interpolate zoo to add all x values from newX
interpolate = function(zoos, newX) {
  newX = newX %>% as.numeric %>% unique
  empty = zoo(order.by = newX)
  zoos %>% map( ~ .x %>% merge(empty) %>% na.approx) %>% return
}

filterRange = function(zoo, range) {
  zoo %>% window(start = range[[1]], end = range[[2]]) %>% return
}

filterRanges = function(zoo, ranges) {
  ranges %>% map( ~ filterRange(zoo, .x)) %>% return
}

# Must be called after interpolate. Filters zoos so that you keep only the portion of the data that appears in all zoos
filterZoosToOverlap = function(zoos) {
  minX = zoos %>% map( ~ .x %>% index %>% reduce(min)) %>% reduce(max)
  maxX = zoos %>% map( ~ .x %>% index %>% reduce(max)) %>% reduce(min)
  zoos %>% map( ~ filterRange(.x, list(minX, maxX))) %>% return
}

# Subtract the y value at x=0 from all y values
normaliseYAbs = function(zoo) {
  zeroVal = zoo %>% getZooValue(0)
  coredata(zoo) = coredata(zoo) - zeroVal
  return(zoo)
}

# Divide all y values by the y value at x=0
normaliseYRel = function(zoo) {
  zeroVal = zoo %>% getZooValue(0)
  coredata(zoo) = coredata(zoo) / zeroVal
  return(zoo)
}

# Subtract all x values by the first x value
normaliseX = function(zoo){
  firstIndex = zoo %>% index %>% first
  index(zoo) = index(zoo) - firstIndex
  return(zoo)
}

# Apply a function index-wise and return the value. Must interpolate first
aggregateZoos = function(zoos, func) {
  times = zoos %>% first %>% index
  data = zoos %>% map(coredata)
  data = zoos %>% transpose
  data = map(data, as.numeric)
  data = map(data, func)
  data = transpose(data)
  data = map(data, as.numeric)
  data = map(data, ~zoo(.x, times))
  return(data)
}

# Convert a list of values to a list of ranges by adding length to each value
makeRanges = function(lst, sub, add){
  lst = lst %>% as.numeric
  subbed = lst - sub
  added = lst + add
  output = list(subbed, added) %>% transpose
  return(output)
}

# Generate a list of ranges for each playtest in a data set
getScareRanges = function(data, sub, add){
  data %>% map( ~ .x$scares) %>% map(~makeRanges(.x, sub, add)) %>% return
}

filterOverrunning = function(zoos){
  zoos %>% keep(~end(.x) <= 6e5) %>% return
}

xOfMinY = function(zoo){
  idx = which.min(zoo)
  x = index(zoo)[[idx]]
  return(x)
}

# Plot one or more zoos
makePlot = function(zoos,
                    main = "",
                    xlab = "",
                    ylab = "") {
  # Figure out the axis ranges
  Xs = zoos %>% map( ~ .x %>% index %>% as.list) %>% flatten
  Ys = zoos %>% map( ~ .x %>% coredata %>% as.list) %>% flatten
  
  minY = Ys %>% reduce(min)
  maxY = Ys %>% reduce(max)
  
  minX = Xs %>% reduce(min)
  maxX = Xs %>% reduce(max)
  
  # Plot an empty plot
  plot(
    1,
    type = "n",
    xlab = xlab,
    ylab = ylab,
    xlim = c(minX, maxX),
    ylim = c(minY, maxY),
    main = main
  )
}

plotLines = function(zoos, col = "black", lty = "solid") {
  zoos %>% walk( ~ lines(.x, col = col, lty = lty))
}

meanAndStdErr = function(vec){
  stdErr = std.error(vec)
  avg = mean(vec)
  vals = c(avg-stdErr, avg, avg+stdErr)
  return(vals)
}

getSeries = function(data, zoos, newX){
  searchRange = 1e4
  length = 1e4
  windowRadius = length/2
  
  scares = getScareRanges(data, 0, searchRange)
  
  scareSearch = map2(zoos, scares, filterRanges)
  mins = scareSearch %>% map(~.x %>% map(xOfMinY))
  ranges = mins %>% map(~makeRanges(.x, windowRadius, windowRadius))
  
  series = zoos %>% interpolate(ranges %>% flatten %>% flatten)
  series = map2(series, ranges, filterRanges) %>% flatten
  series = filterOverrunning(series)
  series = map(series, normaliseX)
  series = map(series, normaliseYAbs)
  
  series = interpolate(series, getAllX(series))
  series = aggregateZoos(series, meanAndStdErr)
  return(series)
}

iZoos = intervention %>% mapcol("edaData") %>% map(toZoo)
cZoos = control %>% mapcol("edaData") %>% map(toZoo)

newX = c(cZoos, iZoos) %>% getAllX %>% append(0) %>% append(6e5)

iSeries = getSeries(intervention, iZoos, newX)
cSeries = getSeries(control, cZoos, newX)

all = c(iSeries, cSeries)

all %>% makePlot(main = "EDA of both groups (Relative)",
                                 xlab = "TimeMs",
                                 ylab = "EDA")

iSeries[2] %>% plotLines(col = "red", lty = "solid")
iSeries[c(1,3)] %>% plotLines(col = "red", lty = "dotted")

cSeries[2] %>% plotLines(col = "blue", lty = "solid")
cSeries[c(1,3)] %>% plotLines(col = "blue", lty = "dotted")

#lines(c(0,0), c(-1e10, 1e10), lty = "dashed")
#lines(c(6e5,6e5), c(-1e10, 1e10), lty = "dashed")

legend(
  "bottomleft",
  legend = c("Control (Mean)", "Intervention (Mean)"),
  fill = c("blue", "red")
)
