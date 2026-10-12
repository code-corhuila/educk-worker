# Python deployment candidate

The Annex J composition selects deploy/Dockerfile.python. Java source remains
available for review/history but is not deployed by that composition. Together
with the Java domain APIs, this supplies two backend implementation languages.

Run python -m unittest discover -s python. Tests cover real HTTP startup without
a database, an unavailable domain followed by retry, and invalid configuration.
The worker follows ADR-009: stateless scheduled HTTP, no database or AMQP consumer.
Its job stays disabled until an approved idempotent domain endpoint is available.
This is not proof of completed notification delivery or the end-to-end sprint goal.
