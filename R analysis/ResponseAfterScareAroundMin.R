
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
  return(scares)
}

iSeries = getSeries(iZoos, iScares)
cSeries = getSeries(cZoos, cScares)
series = c(iSeries, cSeries)

#Plot

allAes = aes(x = iSeries$mean %>% index)

chartDefault + allAes +
  scale_x_continuous(name = "Time after Scare (s)", label = number_format(scale = 1e-3), breaks = seq(0,1e4,len=10)) +
  
  geom_line(y = iSeries$mean %>% coredata, colour=interventionColour) +
  geom_ribbon(aes(ymin = iSeries$lowError, ymax = iSeries$highError), alpha=2/10, fill=interventionColour) +
  
  geom_line(y = cSeries$mean %>% coredata, colour=controlColour) +
  geom_ribbon(aes(ymin = cSeries$lowError, ymax = cSeries$highError), alpha=2/10, fill=controlColour) +
  
  labs(title = "Average Reaction after Scare", y = "Change in EDA")
  


#rm(iSeries, cSeries, series, allAes)
