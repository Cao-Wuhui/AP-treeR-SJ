# LFV-treeR-SJ: Accelerating Set Similarity RS-Joins at Low Thresholds using LFV-trees

This repository contains the source code and datasets for the paper *LFV-treeR-SJ: Accelerating Set Similarity RS-Joins at Low Thresholds using LFV-trees* submitted to ICDE 2025.

## Overview

This repository provides the Java implementation of two algorithms for fast set similarity R-S joins (SSR-SJ):

- **FV-treeR-SJ**: A fast algorithm using FV-tree for set similarity R-S joins.
- **LFV-treeR-SJ**: An enhanced version of FV-treeR-SJ, utilizing a more efficient LFV-tree.

## Dependencies

- OpenJDK 1.8.0
- Apache Hadoop 2.7
- Apache Maven (for building the project)

## Datasets

Due to GitHub's file size upload limitations (100 MB), we are unable to include the datasets in this repository. Please download the datasets required for the experiment from the following website.

- Dblp: [https://www2.cs.sfu.ca/~jnwang/data/adapt/querylog.format(data+query).tar.gz](https://www2.cs.sfu.ca/~jnwang/data/adapt/querylog.format(data+query).tar.gz)
- Kosarak and Accidents: [http://fimi.uantwerpen.be/data/](http://fimi.uantwerpen.be/data/)
- Enron: [https://www.cs.cmu.edu/~./enron/](https://www.cs.cmu.edu/~./enron/)
