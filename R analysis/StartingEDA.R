#  TODO histogram of intervention and control EDA values at 0 - 2 graphs. Compare values. C has more low values, could partly explain the bigger drops in intervention

scares = c(iScares, cScares) %>% map(~length(.x)) %>% unlist
timing = c(intervention, control) %>% map(~.x$participant$timing) %>% unlist

tab = table(scares, timing)

mod = lm(timing ~ scares)
newX <- seq(min(scares), max(scares), length.out=length(scares))
preds <- predict(mod, newdata = data.frame(x=newX), interval='confidence')[,1]

chartDefault +
  stat_smooth(aes(x = scares, y = timing), method = "lm", col = "black", n=1e3) +
  geom_count(aes(x = scares, y = timing)) +
  scale_size_area(max_size = 10)
  

