Check EORI Number
===================

This microservice provides users with a way to:

* check if an [EORI] number beginning with GB (issued by the UK) is valid.
* view the name and address of the business that the [EORI] number is registered to (if the business agreed to share this information).

Requirements
------------

This service is written in [Scala] and [Play], so needs at least a [JRE] to run.

Getting started with Check EORI Number
----------------------------------------

### Step-by-step guide


 1. Clone the git repos to the same location you keep your other services from:

    git clone git@github.com:hmrc/check-eori-number.git
    
    Note: Make sure that service-manager-config is up to date locally

 2. Start the services:
 
        Run the services against the current versions in dev, stop the CHEN service and start manually
        
            sm --start CHEN_ALL -f 
            sm --stop CHEN
            cd check-eori-number
            sbt run

 3. You can use [Check Eori Number Frontend](https://github.com/hmrc/check-eori-number-frontend) or [Check Eori Number API](https://github.com/hmrc/check-eori-number-api) to interact with this service. Or you can integrate with the endpoints yourself.
    
### Endpoints        
 
 | Path                         | Method | Description |
 |:-----------------------------|:-------|:------------|
 | /check-eori/:eoriNumber      | GET    | Checks a single EORI number  |
 | /check-multiple-eori         | POST   | Checks multiple EORI numbers with a maximum of 10 EORI numbers per request|
 
### Results
In a stubbed environment each [EORI] will exhibit different behavior [Please see here for details](https://github.com/hmrc/check-eori-number-stub/#readme)


Unit tests
----------
Run `sbt test` on the terminal to run the unit tests.

Acronyms
---

In the context of this application we use the following acronyms and define their 
meanings. Provided you will also find a web link to discover more about the systems
and technology. 

* [API]: Application Programming Interface

* [HoD]: Head of Duty

* [JRE]: Java Runtime Environment

* [EORI]: Economic Operators Registration and Identification

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
    
[HoD]: http://webarchive.nationalarchives.gov.uk/+/http://www.hmrc.gov.uk/manuals/sam/samglossary/samgloss249.htm   
[API]: https://en.wikipedia.org/wiki/Application_programming_interface
[JRE]: http://www.oracle.com/technetwork/java/javase/overview/index.html
[EORI]: https://ec.europa.eu/taxation_customs/business/customs-procedures/general-overview/economic-operators-registration-identification-number-eori_en
[SCALA]: http://www.scala-lang.org/
[PLAY]: http://playframework.com/