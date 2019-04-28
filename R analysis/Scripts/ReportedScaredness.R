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

model = lm(y ~ x)
p = pValue(model) %>% signif(3)
pText = paste0("P = ", p)

chart = chartDefault +
  scale_x_continuous(name = "Reported Scaredness", breaks = seq(2,5,len=4)) +
  scale_y_continuous(name = bquote('Mean EDA Drop '~(10^3)), label = number_format(scale = 1e-3), limits = c(0, 3e4)) +
  stat_smooth(aes(x = x, y = y), method = "lm", color = "black") +
  geom_text(aes(x = 4.6, y = 14e3, label = pText), color = "red") +
  geom_point(aes(x = x, y = y))
  
print(chart)

rm(getDrop, getMeanDrop, getSeries, scares, y, x, chart, p, pText, model)
