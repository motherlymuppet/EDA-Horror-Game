getSeries = function(zoos, scares){
  windowRadius = lengthAfterScare/2
  
  mins = scares %>% map(~.x %>% map(xOfMinY))
  
  aroundMinBounds = mins %>% map(~makeRanges(.x, windowRadius, windowRadius)) %>% filterManyOverrunning
  zoos = zoos %>% interpolate(aroundMinBounds %>% flatten %>% flatten)
  
  scares = manyZooWindows(zoos, aroundMinBounds) %>% flatten
  scares = map(scares, normaliseX)
  scares = map(scares, normaliseYAbs)
  
  allX = getAllX(scares)
  scares = interpolate(scares, allX)
  scares = aggregateZoos(scares, meanAndStdErr)
  
  scares = scares %>% map(~addX(.x, -windowRadius))
  return(scares)
}

iSeries = getSeries(iZoos, iScares)
cSeries = getSeries(cZoos, cScares)

allAes = aes(x = iSeries$mean %>% index)

chart = chartDefault + allAes +
  scale_x_continuous(name = "Time after Scare Min Point (s)", label = number_format(scale = 1e-3), breaks = seq(-5e3,5e3,len=11)) +
  
  geom_line(aes(y = iSeries$mean %>% coredata, colour="Intervention")) +
  geom_ribbon(aes(ymin = iSeries$lowError, ymax = iSeries$highError, fill="Intervention"), alpha=2/10) +
  
  geom_line(aes(y = cSeries$mean %>% coredata, colour="Control")) +
  geom_ribbon(aes(ymin = cSeries$lowError, ymax = cSeries$highError, fill="Control"), alpha=2/10) +
  
  labs(title = "Average Reaction around Scare min point", y = "Change in EDA")

print(chart)

rm(iSeries, cSeries, allAes, getSeries, chart)
