participantIdx = 6
scareIdx = 6

scare = iScares[[participantIdx]][[scareIdx]] %>% normaliseX %>% normaliseYAbs
x = index(scare)
y = coredata(scare)

troughX = xOfMinY(scare)
troughY = min(scare)

windowedScare = zooWindow(scare, c(0, troughX))

peakX = xOfMaxY(windowedScare)
peakY = max(windowedScare)

maxX = xOfMaxY(scare)
maxY = max(scare)

pointsX = c(troughX, peakX, maxX)
pointsY = c(troughY, peakY, maxY)
pointsLabel = c("Trough", "Peak", "Real Max")

dropSize = peakY - troughY
dropText = paste0("Drop = ", dropSize)

chart = chartDefault +
  scale_x_continuous(name = "Time After Scare (s)", label = number_format(scale = 1e-3), breaks = seq(start(scare),end(scare),len=11)) +
  scale_y_continuous(name = "EDA Change (thousand)", label = number_format(scale = 1e-3)) +
  labs(title = "Example Scare", y = "EDA") +
  geom_line(aes(x = x, y = y)) +
  geom_point(aes(x = pointsX, y = pointsY), color = "red", size = 3, stroke = 1.5, shape = 4) +
  geom_text(aes(x = pointsX, y = pointsY, label = pointsLabel), nudge_y = 5e2, color = "red") +
  geom_segment(aes(x = peakX, xend = peakX, y = peakY, yend = troughY), color = "red", linetype = "dashed") +
  geom_segment(aes(x = peakX, xend = troughX, y = troughY, yend = troughY), color = "red", linetype = "dashed") +
  geom_text(aes(x = peakX, y = (peakY + troughY)/2, label = dropText), color = "red", nudge_x = -8e2)

print(chart)

rm(participantIdx, scareIdx, scare, x, y, troughX, troughY, windowedScare, peakX, peakY, maxX, maxY, pointsX, pointsY, pointsLabel, dropSize, dropText, chart)
