#/ debug: display result and usage

[when]There is a Cheese with=$cheese : Cheese() 
[when]- age is less than {age} = age < {age} 
[when]- type is '{type}' = type == '{type}' 
[when]- country equal to '{country}' = country == '{country}' 
[then]LOG '{message}' = System.out.println('{message}'); 
[then]Label Cheese = $cheese.setName('{message}');