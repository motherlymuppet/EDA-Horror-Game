rm(list = ls())

library("rjson")
library("purrr")
library("dplyr")
library("zoo")
library("plotrix")
library("ggplot2")

loadJson = function() {
  fromJSON(file = "All.txt")
}

data = loadJson()

intervention = data %>% keep( ~ .x$storyteller == "EDA")
control = data %>% keep( ~ .x$storyteller == "REPEAT")

mapCol = function(list, col) {
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
  idx = index(zoo)
  idx %>% keep( ~ .x >= minX && .x <= maxX) %>% return
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
  series = map(series, normaliseYRel)
  
  series = interpolate(series, seq(0:length))
  return(series)
}

#Sort control
interventionIds = intervention %>% map(~.x$participant$id) %>% as.numeric
controlPairs = control %>% map(~.x$participant$pair) %>% as.numeric
control = control[order(match(controlPairs, interventionIds))]

zoos = map(data %>% mapCol("edaData"), toZoo)
newX = zoos %>% getAllX %>% append(0) %>% append(6e5)

iZoos = map(intervention %>% mapCol("edaData"), toZoo)
cZoos = map(control %>% mapCol("edaData"), toZoo)

iSeries = getSeries(intervention, iZoos, newX)
cSeries = getSeries(control, cZoos, newX)

series = map2(iSeries,cSeries,~zoo(coredata(.x)-coredata(.y),index(.x)))

s1 = aggregateZoos(series, meanAndStdErr)

ggplot(s1[[2]], aes(x = index(s1[[2]]))) +
  geom_line(y = coredata(s1[[2]])) +
  geom_line(y = coredata(s1[[1]]), linetype = "dashed") +
  geom_line(y = coredata(s1[[3]]), linetype = "dashed") +
  geom_ribbon(aes(ymin = s1[[1]], ymax = s1[[3]]), alpha=2/10) +
  labs(title = "Title", x = "Time After Scare (ms)", y = "Difference between groups") +
  theme_classic() +
  theme(plot.title = element_text(hjust = 0.5), )
