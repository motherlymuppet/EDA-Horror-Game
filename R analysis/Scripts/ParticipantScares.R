scareCount = iScares %>% map(length) %>% as.numeric
chart = chartDefault + 
  geom_bar(aes(scareCount)) + 
  scale_x_continuous(name = "Scare Count", lim = c(9.5,22.5), breaks = c(10,12,14,16,18,20,22), label = number_format(scale = 1)) +
  labs(y = "Frequency")
print(chart)
rm(scareCount, chart)
