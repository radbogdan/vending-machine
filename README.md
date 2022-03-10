# Vending-machine API

REST API implementation for querying the available users and products.

# Build

The project is built using the gradle command:

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

## Build/Run the test of the application

Created test uses TESTCONTAINERS which supports JUnit tests, providing lightweight, throwaway instances of common
databases, or anything else that can run in a Docker container.

$ gradle clean build
