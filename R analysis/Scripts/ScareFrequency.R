#  TODO histogram of intervention and control EDA values at 0 - 2 graphs. Compare values. C has more low values, could partly explain the bigger drops in intervention

scares = c(iScares, cScares) %>% map(~length(.x)) %>% unlist
timing = c(intervention, control) %>% map(~.x$participant$timing) %>% unlist

tab = table(scares, timing)

mod = lm(timing ~ scares)
newX <- seq(min(scares), max(scares), length.out=length(scares))
preds <- predict(mod, newdata = data.frame(x=newX), interval='confidence')[,1]

chart = chartDefault +
  scale_y_discrete("Scare Timing Answer", lim = c(2:5), labels = c("Too Few", "About Right", "Too Many", "Far Too Many")) +
  scale_x_continuous(name = "Number of times scared", lim = c(10,22), breaks = c(10,12,14,16,18,20,22), label = number_format(scale = 1)) +
  stat_smooth(aes(x = scares, y = timing), method = "lm", col = "black", n=1e3) +
  geom_count(aes(x = scares, y = timing)) +
  scale_size_area(name = "Count", max_size = 10) + 
  labs(title = "How Scare Frequency affected Reported Scare Frequency")

print(chart)
  
rm(scares, timing, mod, newX, preds, chart)

