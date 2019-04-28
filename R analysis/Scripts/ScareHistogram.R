iCalib = iScares %>% map(~.x[1:3])
iTest = iScares %>% map(~.x[4:length(.x)])

cCalib = cScares %>% map(~.x[1:3])
cTest = cScares %>% map(~.x[4:length(.x)])

getScareTimes = function(scares){
  scares %>% 
    flatten %>% 
    map(~start(.x))
}

calib = c(iCalib, cCalib) %>% getScareTimes
test = c(iTest, cTest) %>% getScareTimes

scareTimes = c(calib, test) %>% unlist
fill = c(
  rep("Calibration", length(calib)),
  rep("Test", length(test))
)

chart = chartDefault + 
  scale_fill_manual(
    name = "Scare Type",
    values = c('Calibration' = controlColor,'Test' = interventionColor),
    breaks = c('Calibration', 'Test')
  ) +
  geom_histogram(aes(x = scareTimes, fill = fill), binwidth = 30e3) +
  scale_x_continuous(name = "Time (s)", label = number_format(scale = 1e-3), breaks = seq(start,end,len=11)) + 
  labs(y = "Frequency", fill = "Scare Type")

print(chart)

rm(iCalib, iTest, cCalib, cTest, getScareTimes, calib, test, scareTimes, chart)