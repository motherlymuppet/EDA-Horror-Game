getDrop = function(scare){
  minX = start(scare)
  xOfMin = xOfMinY(scare)
  
  zooBeforeMin = zooWindow(scare, list(minX, xOfMin))
  drop = max(zooBeforeMin) - min(scare)
  return(drop)
}

getMeanDrop = function(scares){
  drops = map(scares, getDrop)
  median = median(drops %>% unlist)
  return(median)
}

getSeries = function(scares){
  scares %>% map(getMeanDrop) %>% return
}

scares = c(iScares, cScares)
y = scares %>% getSeries %>% unlist

x = c(intervention, control) %>% map(~.x$participant$scaredness) %>% unlist


chart = chartDefault +
  scale_x_continuous(name = "Reported Scaredness", breaks = seq(2,5,len=4)) +
  scale_y_continuous(name = "EDA Drop (thousand)", label = number_format(scale = 1e-3), limits = c(0, 3e4)) +
  geom_point(aes(x = x, y = y)) +
  stat_smooth(aes(x = x, y = y), method = "lm", color = "black") +
  labs(title = "Reported Scaredness Correlates with Actual Scaredness")
  
print(chart)

rm(getDrop, getMeanDrop, getSeries, scares, y, x, chart)
