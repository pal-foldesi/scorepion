# Scorepion

Inspired by reading about [sorted sets in Redis](https://redis.io/topics/data-types#sorted-sets).

A leaderboard API with the following constraints and peculiarities:

- Only supports numerical scores
- Uses Redis to store leaderboards
- Uses a [Redis Lua script](https://redis.io/commands/eval) with a Redis sorted set to atomically add a score to and cap a leaderboard
- Uses SQL to store users
- There are no regular users, only admins and guests
- Guests can submit scores and read existing ones
- Admins can also create and delete leaderboards
- Administrators are global administrators, for all leaderboards

## Running locally

1. Clone the repository.
2. Use Docker Compose, either via CLI or via IDE integration, to run the application stack.

N.B.: Certain environment variables must be supplied for the app to run. Defaults have been provided in docker-compose.yml.

## Client integration

Any client that can talk to RESTful APIs can integrate with Scorepion using a leaderboard key. [Squarefall](https://github.com/pal-foldesi/squarefall) is such a client.

## Linting

Linting is done using [SonarLint](https://www.sonarlint.org/). No project-specific settings are used.

## Testing

Tests can be run with ```./gradlew test```.

## REST API Documentation

When running the app locally, the REST API documentation is available via [this link](http://localhost:8080).