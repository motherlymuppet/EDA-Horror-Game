participant = 8
z = iZoos[[participant]]
x = index(z)
y = coredata(z)

scares = iScares[[participant]]

scareTimes = scares %>% map(~start(.x)) %>% as.numeric
scareValues = scares %>% map(~.x[[1]]) %>% as.numeric

chart = chartDefault +
  scale_x_continuous(name = "Time (s)", label = number_format(scale = 1e-3))+#, breaks = seq(start,end,len=11)) +
  scale_y_continuous(name = "EDA (thousand)", label = number_format(scale = 1e-3)) +
  labs(title = "Example Data", y = "EDA") + 
  geom_point(aes(x = scareTimes, y = scareValues), color = "red", size = 3, stroke = 1.5, shape = 4) +
  geom_line(aes(x = x, y = y))

print(chart)

rm(participant, z, x, y, scares, scareTimes, chart)
