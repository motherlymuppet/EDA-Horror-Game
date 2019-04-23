data = fromJSON(file = "All.txt")

intervention = data %>% keep( ~ .x$storyteller == "EDA")
control = data %>% keep( ~ .x$storyteller == "REPEAT")

#Sort
interventionIds = intervention %>% map(~.x$participant$id) %>% as.numeric
controlPairs = control %>% map(~.x$participant$pair) %>% as.numeric
control = control[order(match(controlPairs, interventionIds))]
rm(interventionIds, controlPairs)

start = 0
end = 6e5

# Map a list to one of its columns
mapCol = function(list, col) {
  list %>% map( ~ .x[[col]]) %>% return
}

getScares = function(data){
  data %>% mapCol("scares") %>% return
}

# Convert loaded JSON data to zoos
toZoo = function(edaData) {
  times = edaData %>% names %>% as.numeric
  values = edaData %>% unname %>% as.numeric
  zoo(values, times) %>% return
}




cZoos = control %>% mapCol("edaData") %>% map(toZoo)
iZoos = intervention %>% mapCol("edaData") %>% map(toZoo)



# Convert a list of values to a list of ranges by adding length to each value
makeRanges = function(lst, sub, add){
  lst = lst %>% as.numeric
  start = lst - sub
  end = lst + add
  output = list(start, end) %>% transpose
  return(output)
}

# Generate a list of ranges for each scare in a data set
getScareRanges = function(data, lengthAfterScare){
  data %>% getScares() %>% map(~makeRanges(.x, 0, lengthAfterScare)) %>% return
}



lengthAfterScare = 1e4
iScareTimes = getScareRanges(intervention, lengthAfterScare)
cScareTimes = getScareRanges(control, lengthAfterScare)



filterOverrunning = function(scareTimings){
  scareTimings %>% keep(~.x[[2]] <= end) %>% return
}

filterManyOverrunning = function(scareTimings){
  scareTimings %>% map(filterOverrunning) %>% return
}



iScareTimes = filterManyOverrunning(iScareTimes)
cScareTimes = filterManyOverrunning(cScareTimes)



# Get a list of all x values that appear in multiple zoos
getAllX = function(zoos) {
  zoos %>% map(index) %>% flatten %>% return
}



scareBorders = c(iScareTimes, cScareTimes) %>% flatten %>% flatten
iX = getAllX(iZoos)
cX = getAllX(cZoos)

interpolateValues = c(start, end, scareBorders, iX, cX)



# Linearly interpolate zoo to add all x values from newX
interpolate = function(zoos, newX) {
  newX = newX %>% as.numeric %>% unique
  empty = zoo(order.by = newX)
  zoos %>% map( ~ .x %>% merge(empty) %>% na.approx) %>% return
}



iZoos = interpolate(iZoos, interpolateValues)
cZoos = interpolate(cZoos, interpolateValues)
rm(scareBorders, iX, cX, interpolateValues)



zooWindow = function(zoo, range) {
  zoo %>% window(start = range[[1]], end = range[[2]]) %>% return
}

zooWindows = function(zoo, ranges) {
  ranges %>% map( ~ zooWindow(zoo, .x)) %>% return
}

manyZooWindows = function(zoos, ranges){
  map2(zoos, ranges, zooWindows) %>% return
}



iScares = manyZooWindows(iZoos, iScareTimes)
cScares = manyZooWindows(cZoos, cScareTimes)



getZooValue = function(zoo, time) {
  idx = which(index(zoo) == time)
  coredata(zoo)[[idx]] %>% return
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

# Subtract all x values by the first x value
addX = function(zoo, add){
  index(zoo) = index(zoo) + add
  return(zoo)
}

xOfMaxY = function(zoo){
  idx = which.max(zoo)
  x = index(zoo)[[idx]]
  return(x)
}

xOfMinY = function(zoo){
  idx = which.min(zoo)
  x = index(zoo)[[idx]]
  return(x)
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

meanAndStdErr = function(vec){
  err = std.error(vec)
  avg = mean(vec)
  vals = list(lowError = avg-err, mean = avg, highError = avg+err)
  return(vals)
}

interventionColor = "#1b9e77"
controlColor = "#7570b3"

mainColor = controlColor
altColor = interventionColor

chartDefault = ggplot() +
  scale_colour_manual(
    name = "Group",
    values = c('Intervention' = interventionColor,'Control' = controlColor),
    breaks = c('Intervention', 'Control')
  ) +
  scale_fill_manual(
    name = "Group",
    values = c('Intervention' = interventionColor,'Control' = controlColor),
    breaks = c('Intervention', 'Control')
  ) +
  theme_classic() +
  theme(
    plot.title = element_text(hjust = 0.5),
    legend.position = "bottom"
    )

rm(data, mapCol, toZoo, getScares)
