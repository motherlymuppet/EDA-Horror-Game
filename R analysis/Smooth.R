
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

all = iScares %>% flatten %>% map(normaliseX) %>% map(normaliseYAbs)
x = all %>% map(index) %>% flatten %>% as.numeric
y = all %>% map(coredata) %>% flatten %>% as.numeric
ae = aes(x = x, y = y)

ggplot() + geom_point(ae)
