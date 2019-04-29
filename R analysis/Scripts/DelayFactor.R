func = function(x){
  factor = -0.366
  exp(factor * x) %>% return
}

clamp = function(y){
  max = 3
  min = 1/3
  
  if(y > max){
    return(max)
  }
  if(y < min){
    return(min)
  }
  return(y)
}

x = seq(-5, 5, 0.01)
y = x %>% map(func) %>% map(clamp) %>% unlist

chart = chartDefault +
  geom_path(aes(x = x, y = y)) +
  labs(x = "Standard Deviations Above Mean", y = "Delay Factor")

print(chart)

rm(func, clamp, x, y, chart)