rm(list = ls())
library(rjson)
library(purrr)
library(dplyr)
library(zoo)
library(plotrix)
library(ggplot2)
library(scales)
library(knitr)
library(kableExtra)
library(gridExtra)

fileName = "3.txt"
start = 0
length = 10 #minutes
end = length * 60 * 1000

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


# Convert a list of values to a list of ranges by adding length to each value
makeRanges = function(lst, sub, add){
  lst = lst %>% as.numeric
  start = lst - sub
  end = lst + add
  output = list(start, end) %>% transpose
  return(output)
}



filterOverrunning = function(scareTimings){
  scareTimings %>% keep(~.x[[2]] <= end) %>% return
}

# Get a list of all x values that appear in multiple zoos
getAllX = function(zoos) {
  zoos %>% map(index) %>% flatten %>% return
}



# Linearly interpolate zoo to add all x values from newX
interpolate = function(dataZoo, newX) {
  newX = newX %>% as.numeric %>% unique
  empty = zoo(order.by = newX)
  dataZoo %>% merge(empty) %>% na.approx %>% return
}

zooWindow = function(zoo, range) {
  zoo %>% window(start = range[[1]], end = range[[2]]) %>% return
}

zooWindows = function(zoo, ranges) {
  ranges %>% map( ~ zooWindow(zoo, .x)) %>% return
}

getClosestZooValue = function(zoo, time) {
  idx = which(abs(index(zoo)-time)==min(abs(index(zoo)-time)))
  coredata(zoo)[[idx]] %>% return
}

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

pValue = function (modelobject) {
  if (class(modelobject) != "lm") stop("Not an object of class 'lm' ")
  f <- summary(modelobject)$fstatistic
  p <- pf(f[1],f[2],f[3],lower.tail=F)
  attributes(p) <- NULL
  return(p)
}

rSq = function (model){
  summary(model)$r.squared %>% return
}

gradient = function (model){
  model %>% coef %>% .[[2]] %>% return
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
  theme_classic()+#base_family = "serif") +
  theme(
    plot.title = element_text(hjust = 0.5),
    legend.position = "bottom",
    #legend.margin=margin(rep(0,4)),
    legend.box.margin = margin(t = -12, b = -6)
  )

update_geom_defaults("text", list(family = "serif"))


data = fromJSON(file = fileName)
startTime = data$`Event Data`[[1]]$time
edaData = data$GSR
edaDataTime = edaData %>% map(~.x$time) %>% as.numeric - startTime
edaDataValue = edaData %>% map(~.x$value) %>% as.numeric
dataZoo = zoo(edaDataValue, edaDataTime)
rm(edaData, edaDataTime, edaDataValue)

lengthAfterScare = 1e4
scareTimes = data$`Event Data` %>% head(-1) %>% tail(-1) %>% map(~.x$time) %>% as.numeric - startTime
scareRanges = makeRanges(scareTimes, 0, lengthAfterScare) %>% filterOverrunning
rm(scareTimes, startTime)

scareBorders = scareRanges %>% flatten
dataZoo = interpolate(dataZoo, c(start, end, scareBorders))
rm(scareBorders)

scares = zooWindows(dataZoo, scareRanges)
rm(scareRanges)

mat <- matrix(c(1,2,3,4), 2)
layout(mat, c(1,1), c(1,1))
rm(mat)



plot1z = dataZoo
plot1x = index(plot1z)
plot1y = coredata(plot1z)

plot1scareTimes = scares %>% map(~start(.x)) %>% as.numeric
plot1scareValues = scares %>% map(~.x[[1]]) %>% as.numeric

plot1 = chartDefault +
  scale_x_continuous(name = "Time (s)", label = number_format(scale = 1e-3)) +
  scale_y_continuous(name = bquote('EDA '~(10^3)), label = number_format(scale = 1e-3)) +
  geom_point(aes(x = plot1scareTimes, y = plot1scareValues), color = "red", size = 3, stroke = 1.5, shape = 4) +
  geom_line(aes(x = plot1x, y = plot1y)) +
  labs(title = "EDA Data")







plot2getSeries = function(scares){
  scares = tail(scares, -3)
  scares = map(scares, normaliseX)
  scares = map(scares, normaliseYAbs)
  
  allX = getAllX(scares)
  scares = scares %>% map(~interpolate(.x, allX))
  series = aggregateZoos(scares, meanAndStdErr)
  return(series)
}

plot2series = plot2getSeries(scares)
plot2allAes = aes(x = plot2series$mean %>% index)

plot2 = chartDefault + plot2allAes +
  scale_x_continuous(name = "Time after Scare (s)", label = number_format(scale = 1e-3), breaks = seq(0,1e4,len=11)) +
  scale_y_continuous(name = bquote('EDA Change '~(10^3)), label = number_format(scale = 1e-3)) +
  
  geom_line(aes(y = plot2series$mean %>% coredata)) +
  geom_ribbon(aes(ymin = plot2series$lowError, ymax = plot2series$highError,), alpha=2/10) +
  labs(title = "Average Scare (Absolute)")








plot3zooRange = function(zoo){
  minX = start(zoo)
  
  minY = zoo %>% coredata %>% reduce(min)
  xOfMinY = xOfMinY(zoo)
  
  range = list(minX, xOfMinY)
  zooBeforeMin = zooWindow(zoo, range)
  maxY = zooBeforeMin %>% reduce(max)
  drop = maxY - minY
  
  list(scare = minX, drop = drop) %>% return
}

plot3getSeries = function(scares){
  ranges = scares %>% map(plot3zooRange) %>% map(~list(x = .x$scare, y = .x$drop))
  ranges %>% transpose %>% map(as.numeric) %>% return
}

plot3series = plot3getSeries(scares)

plot3model = function(series){
  mod <- lm(y ~ x, data = series) %>% return
}

plot3lm = plot3model(plot3series)

plot3 = chartDefault +
  scale_x_continuous(name = "Time (s)", label = number_format(scale = 1e-3), breaks = seq(start,end,len=11)) +
  scale_y_continuous(name = bquote('EDA Drop '~(10^3)), label = number_format(scale = 1e-3)) +
  
  geom_point(aes(x = plot3series$x, y = plot3series$y), shape = 4)+
  stat_smooth(aes(x=plot3series$x, y=plot3series$y), method="lm", n=1e3) +
  labs(title = "Desensitisation Over Time")








plot4getSeries = function(scares){
  scares = tail(scares, -3)
  scares = map(scares, normaliseX)
  scares = map(scares, normaliseYRel)
  
  allX = getAllX(scares)
  scares = scares %>% map(~interpolate(.x, allX))
  series = aggregateZoos(scares, meanAndStdErr)
  return(series)
}

plot4series = plot4getSeries(scares)

plot4allAes = aes(x = plot4series$mean %>% index)

plot4 = chartDefault + plot4allAes +
  scale_x_continuous(name = "Time after Scare (s)", label = number_format(scale = 1e-3), breaks = seq(0,1e4,len=11)) +
  scale_y_continuous(name = bquote("EDA Change (%)"), label = number_format(scale = 100)) +
  
  geom_line(aes(y = plot4series$mean %>% coredata)) +
  geom_ribbon(aes(ymin = plot4series$lowError, ymax = plot4series$highError), alpha=2/10) +
  labs(title = "Average Scare (Relative)")





grid.arrange(plot1, plot2, plot3, plot4, ncol = 2)
summary(plot3lm)