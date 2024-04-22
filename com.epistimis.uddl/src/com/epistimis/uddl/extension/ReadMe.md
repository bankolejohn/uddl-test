# Intent
This package should contain classes tha extend the functionality of the grammar generated classes in this project. This functionality may be general purpose.   This package can contain both Java/ XTend and OCL. The functionality in this package should navigate the EMF model, examine data in it and extract data from it.  This is analogous to XTend extension classes/methods (in fact, anything written in XTend can treat them that way).

This package should not contain constraint / validation specific code - i.e. OCL Invariants or Java Validators. Invariants / validators make a judgment as to whether or not data is good, bad or indifferent. Invariants / validators should be located in the constraints / validation packages. The assumption is that invariants and validators will use the functionality in this package.

## NOTE
Predefined / standard FACE files (typically OCL) will not be split even though they contain both navigation and constraints. This is to minimize headaches matching them with the standard if the standard evolves over time.
