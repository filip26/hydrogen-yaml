# Java API for YAML Processing

![Java CI with Maven](https://github.com/filip26/hydrogen-yaml/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/filip26/hydrogen-yaml.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/filip26/hydrogen-yaml/context:java)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Hydrogen YAML is the simplest human-readable data-serialization language inspired by [StrictYAML](https://github.com/crdoconnor/strictyaml). Some YAML features are not intentionally supported.

## Features
- Implicit serialization style
  * autodetection
  * decoupled data model

## Unsupported Features
- Implicit typing
- Duplicate keys
- Explicit tags
- Node anchors and refs
- Flow style (except scalars and simple sequences)
- Complex mapping keys
- Directives

See [What YAML features does StrictYAML remove?](https://hitchdev.com/strictyaml/features-removed/) for more details.

Presence of a removed feature raises an exception during parsing.

## Roadmap
- [x] ~0.1 Document API~
- [x] ~0.2 Writer API~
- [ ] 0.3 Parser API
- [ ] 0.5 Flow Sequence of Scalars
- [ ] 0.6 Multi-documents
- [ ] 0.8 Beans Binding

## Examples

```javascript
YamlNode node = Yaml.createMappingBuilder()
                    .add("key", "value")
                    .add("sequence", Yaml.createSequenceBuilder()
                                         .add("item")
                    ).build();
     
Yaml.createWriter(OutputStream|Writer).write(YamlNode);
```
