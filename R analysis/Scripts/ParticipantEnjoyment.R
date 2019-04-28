iEnjoyment = intervention %>% map(~.x$participant$enjoyment) %>% as.numeric
cEnjoyment = control %>% map(~.x$participant$enjoyment) %>% as.numeric

x = c(iEnjoyment, cEnjoyment) %>% as.vector

group = c(
  rep(1, length(iEnjoyment)),
  rep(2, length(cEnjoyment))
)

fill = c(
  rep("Intervention", length(iEnjoyment)),
  rep("Control", length(cEnjoyment))
)

chart = chartDefault + 
  geom_bar(aes(x = x, group = group, fill = fill), position = position_dodge2(width = 0.9, preserve = "single")) +
  scale_x_discrete("Response", lim = c(4:10)) +
  labs(y = "Frequency", fill = "Group")

print(chart)
rm(iEnjoyment, cEnjoyment, chart, fill, x, group)
