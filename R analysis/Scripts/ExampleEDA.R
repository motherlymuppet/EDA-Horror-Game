participant = 9
z = iZoos[[participant]]
x = index(z)
y = coredata(z)

scares = iScares[[participant]]

scareTimes = scares %>% map(~start(.x)) %>% as.numeric
scareValues = scares %>% map(~.x[[1]]) %>% as.numeric

examplePos = c(1e5, 14e4)
scareTimes = append(scareTimes, examplePos[[1]])
scareValues = append(scareValues, examplePos[[2]])

chart = chartDefault +
  scale_x_continuous(name = "Time (s)", label = number_format(scale = 1e-3))+
  scale_y_continuous(name = bquote('EDA '~(10^3)), label = number_format(scale = 1e-3)) +
  geom_point(aes(x = scareTimes, y = scareValues), color = "red", size = 3, stroke = 1.5, shape = 4) +
  geom_line(aes(x = x, y = y)) +
  geom_text(aes(x = examplePos[[1]] + 3e4, y = examplePos[[2]], label = "= Scare"), color = "red")

print(chart)

rm(participant, z, x, y, scares, scareTimes, chart)
