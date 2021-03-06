# sqs-priority-client
[![][build img]][build]
[![][docs img]][docs]
[![][license img]][license]

Reactive wrapper around the [Amazon SQS](https://aws.amazon.com/sqs/) client that allows reading from multiple queues with weighted priority.

![client-diagram](diagram.png)

## Documentation
For detailed documentation on this library and its features please refer to the [SQS Priority Client User Guide](https://gregwhitaker.github.io/sqs-priority-client/).

## Example
Please see the included [example project](example) for a demonstration on configuring and consuming messages with the sqs-priority-client.

## Bugs and Feedback
For bugs, questions, and discussions please use the [Github Issues](https://github.com/gregwhitaker/sqs-priority-client/issues).

## License
Copyright 2021 Greg Whitaker

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[build]:https://github.com/gregwhitaker/sqs-priority-client/actions/workflows/gradle-build.yml
[build img]:https://github.com/gregwhitaker/sqs-priority-client/actions/workflows/gradle-build.yml/badge.svg

[docs]:https://gregwhitaker.github.io/sqs-priority-client
[docs img]:https://img.shields.io/badge/Documentation-yes-green.svg

[license]:LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg
