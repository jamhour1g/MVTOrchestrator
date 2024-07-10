# MVTOrchestrator an MVT Memory Management Simulator

## Project Overview

This project simulates the Multiple Partitions - Variable Regions memory management technique, also known as MVT (
Multiprogramming with Variable number of Tasks) in IBM machines. It demonstrates the behavior of MVT memory management
in a simulated environment.

## Features

- Simulates a 2GB (2048MB) memory, with 512 MB allocated for the OS
- Reads process information from `ready.txt` and `job.txt` files
- Allocates processes to memory based on the order in `ready.txt`
- Implements FCFS (First-Come, First-Served) scheduling with skip for the job queue
- Utilizes a PCB (Process Control Block) data structure
- Performs memory compaction when the number of holes exceeds 3
- Maintains lists of allocated regions and free holes

## Key Elements

1. Memory allocation and deallocation
2. Process scheduling
3. Memory compaction (defragmentation)
4. Visualization of memory contents

## Current Status and Future Output

The project is currently under active development. While there is no output generated at this time, we are working
towards implementing the following features:

### Planned Output

Once completed, the program aims to provide detailed visual and textual output, including:

- Graphical representation of memory contents
- Current state of ready and job queues
- Information on finished jobs and newly allocated jobs
- Diagrams of memory partitions and free holes
- Before and after views of memory compaction

These features are in progress and will be implemented in future updates. Stay tuned for developments!

## Project Structure

- `src/` - Contains all kotlin source files
- `src/test/` - Contains test files (there are more tests to be implemented in future iterations)
- `ready.txt` - Input file for initial ready queue
- `job.txt` - Input file for job queue
- `README.md` - This file

## Contributing

This is an academic project and is not open for contributions. However, feedback and suggestions are welcome.
