comparePair = function(iScare, cScare){
  both = list(iScare, cScare)
  
  allX = getAllX(both)
  both = interpolate(both, allX)
  iScare = both[[1]]
  cScare = both[[2]]
  
  idx = index(iScare)
  
  iData = coredata(iScare)
  cData = coredata(cScare)
  data = cData - iData
  
  zoo(data, idx) %>% return
}

prepare = function(scares){
  scares %>% map(normaliseX) %>% map(normaliseYRel) %>% return
}

dropCalibration = function(scares){
  scares %>% tail(-3) %>% return
}

iFlatScares = iScares %>% map(dropCalibration) %>% flatten %>% prepare
cFlatScares = cScares %>% map(dropCalibration)  %>% flatten %>% prepare

series = map2(iFlatScares, cFlatScares, comparePair)
series = series %>% interpolate(., getAllX(.))
series = series %>% aggregateZoos(meanAndStdErr)

allAes = aes(x = series$mean %>% index)

chart = chartDefault + allAes +
  scale_x_continuous(name = "Time after Scare (s)", label = number_format(scale = 1e-3), breaks = seq(0,1e4,len=11)) +
  scale_y_continuous(name = "Difference (% points)", label = number_format(scale = 100)) +
  
  geom_line(aes(y = series$mean %>% coredata)) +
  geom_ribbon(aes(ymin = series$lowError, ymax = series$highError), alpha=2/10, fill="black")

print(chart)

rm(comparePair, prepare, iFlatScares, cFlatScares, series, allAes, chart, dropCalibration)
