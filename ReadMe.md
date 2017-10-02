# De-Centralized Model for Drone Traffic
This is the code for a Repast simulation of a agent based model for drone traffic management. There is no centralized visibility and the drones must communicate with each other in order to navigate to their destinations without collision

## The Simulation
When the simulation is running, it starts off with a cubic space with some landing stations at the floor marked in blue. Drones are initially suspended at random points within the space. Once the simulation is initialized, they achieve a flight altitude and commence navigation towards a random landing station. They repeat this indefinitely, moving from one station to the next randomly. The following link will show an example run of the simulation:

[![IMAGE ALT TEXT HERE](https://i.ytimg.com/vi/f2I1v5mUtDE/hqdefault.jpg?sqp=-oaymwEXCNACELwBSFryq4qpAwkIARUAAIhCGAE=&rs=AOn4CLCNlrnviZJQ5TjZnLhIgE1cokzZMw)](https://www.youtube.com/watch?v=f2I1v5mUtDE)

## Design
The design is split into 3 layers as the following diagram shows:
![alt text](https://github.com/Ravi5ingh/DroneSpace/blob/master/GitImages/DesignMod.png?raw=true)

### Environment Layer
The environment layer contains static objects within the simulation and also houses the simulation engine.
#### Simulation Engine
Repast simulations are based on a discreet chronology which means it is played as a series of discreet frames. The simulation engine is a basic Physics engine. The way it works is by determining where any given object will be in the next frame of the simulation given the object, thrust vectors, and drag co-efficients.
To work properly, it needs to properly model the passage of time. To do this, there is a time reolution parameter embedded in the simulation which is basically just the number of simulation frames that correspond to 1 second in the simulation. There is another parameter (Model_TimeResolutionAutoTune). If you set this to true, the simulation will regularly sync 1 second in the real world with 1 second in the simulation at runtime. This allows the simulation to render in the most effective way given that frames/second can be wildly different depending on the load on the system. The following diagram illustrates how movement is modelled in the system:
![alt text](https://github.com/Ravi5ingh/DroneSpace/blob/master/GitImages/PositionVsTime.png?raw=true)
### Agent Layer
This layer contains the drones and navigators, both of which are technically agents in the simulation. Every agent contains a navigator object which is responsible for the technical details of the navigation task. The agent itself is responsible for communication with other drones as described below.
#### Navigator
The navigator is responsible for getting to its prescribed waypoints without colliding with other drones. It does this by constantly modifying the thrust vectors of the drone. The navigator uses the APF (Artificial Potential Fields) path planning technique to achieve this in which a robot imagines that there is a repulsive potential field around the obstacle. The following diagram illustrates this:
![alt text](https://github.com/Ravi5ingh/DroneSpace/blob/master/GitImages/APFDiagram.png?raw=true)
##### Local Minima Problem
The biggest drawback of APF is that robots using this technique are prone to getting stuck in local minima. In this simulation this was observed when multiple drones were simultaneously trying to navigate to a common waypoint. They were perpetually stuck in a cloud as shown below:
![alt text](https://github.com/Ravi5ingh/DroneSpace/blob/master/GitImages/5LocalMinima.png?raw=true)
#### Drone
The solution to this is to instill greater intelligence in the drone itself so it can communicate and resolve issues with surrounding drones. A protocol was devised to do this. The basic intuition behind this that drones should follow other drones heading to the same waypoint once they detect a conflict. Once this idea is extended it forms chains of drone spread out through the airspace. This was the first instance when emergent behaviour was observed in this simulation. The following is a screeni so its not clear but it captures an instance of this behavior
![alt text](https://github.com/Ravi5ingh/DroneSpace/blob/master/GitImages/5NoLocalMinima.png?raw=true)
### Repast Layer
The Repast Layer is where the simulation hooks into the Repast libraries. Repast was primarily used for visualization and actually running the simulation.
## Running It
Running this simulation is not very straightforward but all the steps you need to take are detailed in this PDF doc (http://repast.sourceforge.net/docs/RepastJavaGettingStarted.pdf)
