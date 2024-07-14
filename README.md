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

The project has made significant progress with the implementation of the first user interface (UI).
This UI provides valuable insights into the program's behavior and memory management processes.

### Current Output

The program now generates the following output:

1. Visualization of memory contents:
    - Graphical representation of allocated memory blocks
    - Clear depiction of memory utilization

2. Real-time Queue State:
    - Display of current states of ready and job queues
    - Shows process names and their duration in memory

3. Job Status Monitoring:
    - Tracks when jobs are added to the ready and job queues
    - Monitors job removal and the creation of holes in the "free list"

### Planned Future Enhancements

While significant progress has been made, we are continuing to work on further improvements:

- Include project documentation
- Increase the project's test coverage
- Enhanced graphical representations of memory partitions and free holes
- Before and after views of memory compaction
- More detailed information on finished jobs and newly allocated jobs

These features are in progress and will be implemented in future updates. Stay tuned for further developments!

## Project Structure

- `src/` - Contains all kotlin source files
- `src/test/` - Contains test files (there are more tests to be implemented in future iterations)
- `src/test/resources/ready.txt` - Input file for initial ready queue
- `src/test/resources/jobs_correct_format.txt` - Input file for job queue
- `README.md` - This file

## Contributing

This is an academic project and is not open for contributions. However, feedback and suggestions are welcome.
