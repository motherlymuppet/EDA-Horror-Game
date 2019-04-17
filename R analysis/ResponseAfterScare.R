allScares = data %>% getScareRanges(length) %>% flatten %>% flatten
newX = c(cZoos, iZoos) %>% getAllX %>% append(0) %>% append(6e5) %>% append(allScares)

meanAndStdErr = function(vec){
  stdErr = std.error(vec)
  avg = mean(vec)
  vals = c(avg-stdErr, avg, avg+stdErr)
  return(vals)
}

getSeries = function(data, zoos, newX, length){
  scares = data %>% getScareRanges(length)
  
  series = interpolate(zoos, newX)
  series = map2(series, scares, filterRanges) %>% flatten
  series = filterOverrunning(series)
  series = map(series, normaliseX)
  series = map(series, normaliseYAbs)
  
  newX = getAllX(series)
  series = interpolate(series, newX)
  series = aggregateZoos(series, meanAndStdErr)
  return(series)
}

iSeries = getSeries(intervention, iZoos, newX, length)
cSeries = getSeries(control, cZoos, newX, length)

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
