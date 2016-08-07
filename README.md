# Vivarium

Vivarium is an artificial life simulation, and how I spend much of my time
outside of work. If you want to see what it looks like, http://vivarium.io/life/
hosts a runnable visualizer. If you want to learn more about the what, why, and
how, http://vivarium.io/articles/ has a collection of short writings.

## Motivation

The project is based on the hypothesis that a genetic algorithm is limited by
its fitness function, and that designing a sufficient fitness function for a
poorly understood task is unapproachably difficult. It is my hope that as
computational power continues to grow, lessons learned from these simulations
will be useful in building more powerful generative models.

## Development Environment Setup
1. Run `gradle eclipseSetup`.
1. Import all projects into Eclipse as existing Eclipse projects. For most development, this should be sufficient.
1. Running any database test code requires installing psql, and setting up a user named test with sufficient privelages on a database named test and with password test).

## Running Vivarium
If you would like to run Vivarium and run into any problems, please feel free
to contact me.
* The desktop visualizer can be run with the command `gradle vivarium-desktop:run`.
* The web visualizer can be run with the command `gradle vivarium-html:superDev`, and then navigating to `http://localhost:8080/html/index.html` in a browser.
* Scripts can be built with the command `gradle vivarium-scripts:build` and run with `java -jar PATH_TO_JAR (options)` 

