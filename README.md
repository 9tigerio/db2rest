DB2Rest is a modern low code REST DATA API platform that automatically creates a secure REST API endpoint
for your databases making it easy to build intelligent applications 30x faster.  No ORM, no code generation = FAST!
 
It combines existing/new databases and data-sources with language models (LM/LLMs) and vector stores to rapidly 
deliver context-aware, reasoning applications without any vendor lock-in. :bulb:

:star: If you find DB2Rest useful, please consider adding a star on GitHub! Your support motivates us to add new exciting features.


![Number of GitHub contributors](https://img.shields.io/github/contributors/kdhrubo/db2rest)
[![Number of GitHub issues that are open](https://img.shields.io/github/issues/kdhrubo/db2rest)](https://github.com/kdhrubo/db2rest/issues)
[![Number of GitHub stars](https://img.shields.io/github/stars/kdhrubo/db2rest)](https://github.com/kdhrubo/db2rest/stargazers)
![Number of GitHub closed issues](https://img.shields.io/github/issues-closed/kdhrubo/db2rest)
![Number of GitHub pull requests that are open](https://img.shields.io/github/issues-pr-raw/kdhrubo/db2rest)
![GitHub release; latest by date](https://img.shields.io/github/v/release/kdhrubo/db2rest)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/kdhrubo/db2rest)
[![GitHub license](https://img.shields.io/github/license/kdhrubo/db2rest)](https://github.com/kdhrubo/db2rest)
![GitHub top language](https://img.shields.io/github/languages/top/kdhrubo/db2rest)
![Docker Pulls](https://img.shields.io/docker/pulls/kdhrubo/db2rest)
[![Follow us on X, formerly Twitter](https://img.shields.io/twitter/follow/db2rest?style=social)](https://twitter.com/db2rest)

# Website

[https://db2rest.com](https://db2rest.com)

# Latest Release

| Release              | Download/Docker Pull                                                             |
|----------------------|----------------------------------------------------------------------------------|
| 1.6.0                | [DB2Rest-1.6.0](https://download.db2rest.com/db2rest-1.6.1.jar)                  |
| 1.6.0 (Docker Image) | ` docker pull kdhrubo/db2rest:v1.6.0 ` or ` docker pull kdhrubo/db2rest:latest ` |


# Previous Release

| Release                                    | Download/Docker Pull                                                              |
|--------------------------------------------|-----------------------------------------------------------------------------------|
| 1.5.1                                      | [DB2Rest-1.5.1](https://download.db2rest.com/db2rest-1.5.1.jar)                   |
| 1.5.1 (Docker Image)                       | ` docker pull kdhrubo/db2rest:v1.5.1 `  |

# Last Stable Oracle 9i Release

| Release                                    | Download/Docker Pull                                                              |
|--------------------------------------------|-----------------------------------------------------------------------------------|
| Oracle9i - 1.2.3 <b><mark>Final</mark></b> | [DB2Rest-Oracle9i-1.2.3](https://download.db2rest.com/db2rest-oracle9i-1.2.3.jar) |

# Quick start guides

[On premise / On Virtual Machines installation guide](https://db2rest.com/docs/intro).

[Docker based installation guide](https://db2rest.com/docs/run-db2rest-on-docker).


# Use Cases 

## Accelerate Application Delivery

DB2Rest provides instant REST API(no code generation) to boost development by 30x. No need to write any code and best practices are built-in saving engineering teams
months of effort and cost. 

## Faster innovation with Gen AI

DB2Rest works hand in glove with modern vector databases and LLM implementations to provide consistent Web APIs to deliver smart applications.

## Secure Database Gateway

DB2Rest works as a secure database gateway. This helps enterprises to open up data to internal developers and partners in a safe and agile way which was not possible earlier.

## Simplify Integration, Secure data exchange

Often enterprises export data from databases and share large files using SFTP, S3 etc. This process is slow, complex, error-prone and often very costly.
It requires heavy maintenance cost and it is not possible to share data in realtime. 

Using DB2Rest, it is possible to simplify the process and allow secure data exchange with other parts of the organization without 
writing a single line of code. There is no direct database based point to point integration and data is available to query anytime. 


# Supported Databases

- **PostgreSQL** 
- **MySQL**
- **MS SQL Server**
- **Tembo PostgreSQL** 
- **Oracle**  (Including 9i, 10g)
- **DigitalOcean PostgreSQL** 
- **DigitalOcean MySQL** 
- **AWS RDS Postgres** 
- **AWS RDS MySQL**
- **MariaDB**  
- **CockroachDB**
- **Neon**

# Planned Database Support

- [PostgresML](https://postgresml.org/)
- TursoDB
- **Yugabyte**
- **PlanetScale**
- **CrunchyData**
- **Singlestore**
- **Nile** (Planned)
- **MindsDB**
- [KDB](https://kdb.ai/)
- [Zilliz](https://zilliz.com/)
- [AstraDB](https://www.datastax.com/products/datastax-astra)
- [Vespa](https://vespa.ai/)
- Amazon Lightsail PostgreSQL
- Amazon Lightsail MySQL

# Contributing
Feel like contributing? That's awesome! We have a [contributing guide](https://github.com/9tigerio/db2rest/blob/master/CONTRIBUTING.md) to help guide you.

Our docsite lives in a [separate repo](https://github.com/9tigerio/db2rest-web). If you're interested in contributing to the documentation, check out the docsite contribution guide.

# Building
Use `mvn verify` or `mvn clean package` , etc. from repo root folder, and pass in a value for `revision` to override the current default version in POMs:
`mvn -Drevision="1.5.4-SNAPSHOT" clean package -DskipTests`

# Testing
Running tests simply requires a Docker daemon running, where the build will automatically pull and run testcontainers for the database tests.


# Support

*Connect on Discord*

[![](https://dcbadge.vercel.app/api/server/kqeDatPGwU?theme=discord)](https://discord.gg/kqeDatPGwU)

# Contact

<help@db2rest.com>


# Roadmap

Refer to [open roadmap](https://db2rest.com/roadmap/) items.

# IDE Sponsor

![JetBrains logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.svg)


