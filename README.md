# Vending-machine API

REST API implementation for querying the available users and products.

# Build

The project is built using the embedded maven wrapper:

$ gradle clean build

in the root of the repo.

# Local Development Setup

## Prerequisites

The project uses Mongo as a database on docker.

To set it up for local development you need to run:

$ docker-compose up -d

in the project root directory.

## Starting the application

$ gradle bootRun

will start up the project.

## Buld/Run the test of the application

Created test uses TESTCONTAINERS which supports JUnit tests, providing lightweight, throwaway instances of common
databases, or anything else that can run in a Docker container.

Run the following command to make sure that you don't have a running mongo on docker machine.

$ docker-compose down

in the project root directory.

$ gradle clean build
