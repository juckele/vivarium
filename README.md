# Vivarium

Vivarium is an artificial life simulation, and how I spend much of my time
outside of work. If you want to see what it looks like, http://vivarium.io/life/
 hosts a runnable visualizer. If you want to learn more about the what, why, and
 how, http://vivarium.io/articles/ has a collection of short writings.

## Motivation

The project is based on the hypothesis that a genetic algorithm is limited by
its fitness function, and that designing a sufficient fitness function for a
poorly understood task is unapproachably difficult. It is my hope that as comput
ational power continues to grow, lessons learned from these simulations will be 
useful in building more powerful generative models.

## Running Vivarium

If you would like to run Vivarium, please feel free to contact me if you have an
y problems. The basic set-up requires running `gradle eclipseSetup` and then imp
orting the projects into Eclipse as existing Eclipse projects. Running any datab
ase code requires some additional setup (requires psql, a user named vivarium wi
th sufficient privelages on a database named vivarium and with password lifetest
).

## License

Copyright © 2014 John H Uckele. All rights reserved.