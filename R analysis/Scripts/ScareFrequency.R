#  TODO histogram of intervention and control EDA values at 0 - 2 graphs. Compare values. C has more low values, could partly explain the bigger drops in intervention

scares = c(iScares, cScares) %>% map(~length(.x)) %>% unlist
timing = c(intervention, control) %>% map(~.x$participant$timing) %>% unlist

tab = table(scares, timing)

model = lm(timing ~ scares)
p = pValue(model) %>% signif(3)
pText = paste0("P = ", p)

chart = chartDefault +
  scale_y_discrete("Response", lim = c(2,3,4,5), breaks = c(2,3,4,5), labels = c("Too Few", "About Right", "Too Many", "Far Too Many")) +
  scale_x_continuous(name = "Scare count", lim = c(10,22), breaks = c(10,12,14,16,18,20,22), label = number_format(scale = 1)) +
  stat_smooth(aes(x = scares, y = timing), method = "lm", col = "black", n=1e3) +
  geom_count(aes(x = scares, y = timing)) +
  geom_text(aes(x = 19, y = 4.5, label = pText), color = "red") +
  coord_cartesian(ylim = c(2,5)) +
  labs(scale = "Frequency")

print(chart)
  
rm(scares, timing, chart, p, pText, model)