# fuzzy-decision-tree
A Java decision tree implementation which uses fuzzy membership functions

### Command-line Usage

Generate Java code from decision tree YAML file:

```bash
    
    $ java -jar fuzzy-decision-tree <yaml-file>

```

### Programmatic Usage

Load a function object from from decision tree YAML file and execute it:

```java
    
    import com.bc.dectree.DecTreeDoc;
    import com.bc.dectree.DecTreeFunction;
    
    // Load decision tree function
    //
    DecTreeDoc doc = DecTreeDoc.parse(yamlFile);    
    DecTreeFunction function = DecTreeFunction.load(doc);
    
    // Call decision tree function
    //
    double inputs[] = new double[function.getInputSize()];
    // Set inputs...
    double outputs[] = new double[function.getOutputSize()];
    function.apply(inputs, outputs);

```

### Decision Tree YAML Format

```yaml
    
    name: <fully-qualified-java-class-name>
    version: <dectree-yaml-version-string>  # (optional, default is "1.0")
    
    types:
      <type>:
        <property>: <membership-function>
        ...
      ...
    
    inputs:
      <input>: <type> | number | boolean
      ...
          
    outputs:
      <output>: boolean
      ...

    derived:
      <derived> = <expression>: <type> | number | boolean
      ...

    rules:
      - if <condition>:
          <body>
        else if <condition>:
          <body>
        else:
          <body>
      
      ...

```
