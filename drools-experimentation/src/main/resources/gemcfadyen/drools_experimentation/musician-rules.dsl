#/ debug: display result and usage

[when]There is a musician who plays the "{instrument}"=$m : Musician(instrument=="{instrument}")
[when]Musician is at least {age} years old and lives in "{area}"=Musician(age >= {age}, location == "{area}")
[then]Log "{message}"=System.out.println("{message}"); 
[then]set player to available=((java.util.List)drools.getWorkingMemory().getGlobal("availableMusicians")).add($m); 
[when]And=and
