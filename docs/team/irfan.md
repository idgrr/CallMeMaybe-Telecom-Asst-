---
layout: page
title: Irfan's Project Portfolio Page
---

### Project: CallMeMaybe

CallMeMaybe (CMM) is a **desktop app** centered for Telemarketers in aiding them in customer contact management.
The in-built tracking functionality serves as a reminder to reach back on previously unreachable customers.
Importing and exporting of existing customer database is also supported by CMM to facilitate team-based environments.
The user interacts with it using a CLI, and it has a GUI created with JavaFX. This is written in Java, and has about
16 kLoC. The development of this software was used as a medium for students to apply Software Engineering principles
taught during the Software Engineering Module CS2103T.

Given below are my contributions to the project.

* **New Feature**: Added Importing functionality
  * What it does: Allows users to import contacts CMM's database quickly. CMM currently imports from Excel Files only
  * Justification: Based on the user story, a telemarketer receives his/her daily call list in an Excel sheet. Function
    implementation required an in-depth analysis of design alternatives. This function is an integral part in  ensuring
    a smooth transition between the manager and the telemarketer. This process reduces learning curve, as well as overall
    time of adding in contacts (previously the two options were to either learn json or to manually input  all the data) 
  * Highlights: This enhancement affects existing commands and commands to be added in the future. The type of data
    imported will affect what and how the commands interact with the data found in CMM. This enhancement will also be
    relevant and often updated as long as CMM uses a local storage, and the company  processes  include  Excel
    sheets  in storing customer data. The implementation was challenging as it required constant updating and tweaking
    as the team's definition of a valid person to be imported constantly changed throughout the project
  * Credits: *The previous iteration CallMeMaybe (AddressBookLevel3) 's JSONstorage helped me structure my ImportExport
    feature. This taught me how to follow an existing architectural style while implementing a new feature. It also
    helped me rethink the way I approach code abstractions way before the CS2103T's lecture of code architecture*

* **New Feature**: Added Exporting functionality
  * What it does: Allows users to export contacts CMM's database quickly. CMM currently export to Excel Files only
  * Justification: Based on the user story, a telemarketer returns the daily call list in an Excel sheet to the manager.
    This function is an integral part in ensuring a smooth transition between telemarketers back to managers. This
    is achieved by the seamless data conversion between Excel and CMM. This is important as managers will accept Excel
    files. The Export function also acts as a safety net should telemarketers wish to start a new database with imported
    contacts. CMM will immediately export the current database in an Excel file before wiping and introducing new
    contacts into the database.
  * Highlights: This enhancement affects existing commands and commands to be added in future. It required an in-depth 
    analysis of design alternatives. The implementation too was challenging as it required changes to existing commands.
  * Credits: *The previous iteration CallMeMaybe (AddressBookLevel3) 's JSONstorage helped me structure my ImportExport
    feature. This taught me how to follow an existing architectural style while implementing a new feature. It also
    helped me rethink the way I approach code abstractions way before the CS2103T's lecture of code architecture*
  
* **Code contributed**: [RepoSense link](https://nus-cs2103-ay2122s1.github.io/tp-dashboard/#breakdown=true&search=idgrr)

* **Project management**:
  * Managed releases `v1.3` - `v1.4` (3 releases) on GitHub

* **Enhancements to existing features**:
  * Updated the GUI resizing functionality to ensure that features can always be displayed
  * Wrote additional tests for existing features such as CsvUtil, CsvAdaptedPerson, CsvAddressBook

* **Documentation**:
  * User Guide:
    * Added documentation for the features `import` and `export` and `findAny`
      PR for reference:
      [findAny](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/131/files)
      [import](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/109/files)
      [export](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/97/files)
  * Developer Guide:
    * Added implementation details of the `import` and `export` feature.
      [ImporExport](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/96/files)
      [Usecases](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/32/files)

* **Community**:
  * PRs reviewed (with non-trivial review comments):
    [Reviewing find command](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/175)
    [Reviewing Add command](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/89)
    [Reviewing DG](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/93)
    [Reviewing Addition of new Attributes](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/67)
    [Reviewing testcases](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/38)
    [Reviewing Storage](https://github.com/AY2122S1-CS2103T-T13-4/tp/pull/34)
    
