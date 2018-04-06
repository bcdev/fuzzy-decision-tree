# fuzzy-decision-tree
A Java decision tree implementation which uses fuzzy membership functions. 

Find a Python implementation [here](https://github.com/forman/dectree).

## Command-line Usage

Generate Java code from decision tree YAML file:

```bash
    $ java -jar fuzzy-decision-tree <yaml-file>
```

## Programmatic Usage

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

## YAML Format

### Example

Here is a somwehat numb but illuminating RGB-image classification (result is [here](https://github.com/forman/dectree/blob/master/examples/im_classif/im_classif.ipynb)):

```yaml

    types:
      radiance:
        LOW:       inv_ramp(x1=0, x2=127)
        MIDDLE:    triangular(x1=0, x2=127, x3=255)
        HIGH:      ramp(x1=127, x2=255)
        VERY_HIGH: ramp(x1=180, x2=220)
    
    inputs:
      red:   radiance
      green: radiance
      blue:  radiance
    
    derived:
      mean = (red + green + blue) / 3: radiance
    
    outputs:
      grey:     boolean
      yellow:   boolean
      dark_red: boolean
      dark:     boolean
      not_dark: boolean
      cloudy:   boolean
      mean:     radiance
    
    rules:
      - if mean is VERY_HIGH:
          cloudy: TRUE
    
      - if red is VERY_HIGH and green is VERY_HIGH and blue is VERY_HIGH:
          cloudy: TRUE
    
      - if red is MIDDLE and green is MIDDLE and blue is MIDDLE:
          grey: TRUE
    
      - if red is HIGH and green is HIGH or red is MIDDLE and green is MIDDLE:
          if blue is LOW:
            yellow: TRUE
    
      - if red is MIDDLE or red is LOW and green is MIDDLE or green is LOW:
          if blue is not LOW and blue is not MIDDLE and blue is not HIGH:
            dark_red: TRUE
    
      - if red is LOW and green is LOW and blue is LOW:
          dark: TRUE
        else:
          not_dark: TRUE

```

### Syntax

```yaml
    name: <fully-qualified-java-class-name>
    
    version: <dectree-yaml-version-string>  # (optional, default is "1.0")
    
    types:
      <type>:
        <property>: <membership-function>
        <property>: <membership-function>
        ...
      ...
    
    inputs:
      <input>: <type> | number | boolean
      <input>: <type> | number | boolean
      ...
          
    outputs:
      <output>: boolean
      <output>: boolean
      ...

    derived:
      <derived> = <expression>: <type> | number | boolean
      <derived> = <expression>: <type> | number | boolean
      ...

    rules:
      - <rule>
      - <rule>
      ...

```

where

`<rule>` ::=
```yaml
    if <condition>:
        <block>
    else if <condition>:     # optional
        <block>
    else if <condition>:     # optional
        <block>
    ...
    else:                    # optional
        <block>
```

`<block>` ::=
```yaml
    <rule> | <assignment>
    <rule> | <assignment>
    ...
```

`<assignment>` ::= 
```yaml
    <output>: TRUE | FALSE
``` 

`<condition>` ::=
```java
    ( <condition> )
    | <variable> is <property> 
    | <variable> is not <property> 
    | not <condition>
    | <condition> and <condition>
    | <condition> or <condition>
```

`<variable>` ::= 
```
    <input> | <derived> | <output>
```



