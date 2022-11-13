# tree_printer

A Java class for printing binary trees as ASCII text

It hasn't been optimized for run time efficiency, but since we're talking about printing in ASCII, I figured it's not going to be used on very large trees. It does have some nice features though.

1. It makes efficient use of space in that a large subtree extends under a smaller one as much as possible.
2. It's generic, working for any binary tree data objects, as long as you can provide functions (lambda functions will do) to get a nodes label as a String, and to get the left and right sub-nodes.
3. There's a parameter to set the minimum horizontal space between node labels.
4. Node labels are strings of arbitrary length.
5. In addition to a method for printing a single tree, there's a method for printing a list of trees horizontally across the page (with a parameter for page width), using as many rows as necessary.
6. There's an option to print trees with diagonal branches (diagonal unicode box drawing characters) or with horizontal branches (using unicode box drawing characters). The latter is more compact and makes tree levels more visually clear.
7. It supports basic ANSI escape sequences for colored output to terminal.
8. It works.

Some [demo/test programs](src/test/java) are included.

## Usage

### Gradle:

Use `sourceControl` block in your `settings.gradle.kts`

```kotlin
// in settings.gradle.kts:
sourceControl {
    // without the `uri(...)` if you use Groovy
    gitRepository(
        uri("https://github.com/billvanyo/tree_printer.git")
    ) {
        producesModule(
            "tech.vanyo:tree_printer:1.1"
        )
    }
}
```

and add the dependency in your buildscript `build.gradle.kts` dependencies block:

```kotlin
dependencies {
    implementation(
        "tech.vanyo:tree_printer:1.1"
    )
}
```

## Details

The TreePrinter object has two methods for printing binary trees as ASCII text. `printTree(tree)` prints a single tree.
`printTrees(trees, lineWidth)` prints a list of trees horizontally across the page, in multiple rows if necessary.

The TreePrinter object has a few settable parameters affecting how it prints trees:

- A positive integer parameter `labelGap` specifies the minimum number of horizontal spaces between any two node labels in the tree.
- `colGap` and `rowGap` specifies spacing between trees when using `printTrees(trees, lineWidth)` 
- A boolean parameter `squareBranches` determines whether the tree is drawn with horizontal branches (using ascii box drawing characters) or diagonal branches (using slash and backslash characters). 
- The boolean `lrAgnostic` parameter only affects trees drawn with square style. Its effect is that tree nodes with only a single subtree are drawn with a straight down vertical branch, providing no indication of whether it is a left or right subtree.
- `usePlaceholder` replaces empty labels with placeholders

## Examples

A few test/demo programs are included. For instance, the program EnumTrees can be used to print an enumeration of all binary trees of a given size. All trees of size 5, labeled with number words (one, two, etc) is printed as:

```bash
# use gradlew.bat on windows
./gradlew :testLogging --tests *EnumTrees
```

This produces output like:

```
one           one         one          one        one       one         one       one          one      
  \             \           \            \          \         \           \         \            \      
  two           two         two          two        two      three       three      four         four   
    \             \           \            \          \       / \         / \       / \          / \    
   three         three        four         five       five  two four    two five  two five      /   \   
      \             \         / \          /          /           \         /       \        three  five
      four          five     /   \      three       four          five    four     three      /         
        \           /     three  five      \        /                                       two         
        five      four                     four  three                                                  



one        one       one       one         one        two             two          two          two   
  \          \         \         \           \        / \             / \          / \          / \   
  five       five      five      five        five    /   \           /   \       one four     one five
  /          /         /         /           /     one  three      one  three        / \          /   
two        two      three      four        four            \               \        /   \      three  
  \          \       / \       /           /               four            five  three  five      \   
 three       four  two four  two        three                \             /                      four
    \        /                 \         /                   five        four                         
    four  three               three    two                                                            



   two      three         three         three         three      four      four         four  
   / \       / \           / \           / \           / \       / \       / \          / \   
 one five  one four       /   \        two four      two five  one five  one five     two five
     /       \   \       /     \       /     \       /   /       \         \          / \     
   four      two five  one     five  one     five  one four      two      three      /   \    
   /                     \     /                                   \       /       one  three 
three                    two four                                 three  two                  



     four            four       five        five      five      five      five       five         five
     / \             / \        /           /         /         /         /          /            /   
    /   \           /   \     one         one       one       one       one        two          two   
 three  five     three  five    \           \         \         \         \        / \          / \   
  /               /             two         two      three      four      four    /   \       one four
one             two               \           \       / \       /         /     one  three        /   
  \             /                three        four  two four  two      three            \      three  
  two         one                   \         /                 \       /               four          
                                    four   three               three  two                             



    five        five      five      five         five        five          five
    /           /         /         /            /           /             /   
 three       three      four      four         four        four          four  
  / \         / \       /         /            /           /             /     
one four    two four  one       one          two        three         three    
  \         /           \         \          / \         /             /       
  two     one           two      three      /   \      one           two       
                          \       /       one  three     \           /         
                         three  two                      two       one         
```

[RandomTree](src/test/java/RandomTree.java) can be used to print a single randomly generated tree. The following is an example of
the
same
tree
printed 4 different ways, with horizontal spacing of 1 and of 3, and with diagonal and horizontal branches. To
run this from the command line using maven type:

```
# use gradlew.bat on windows
./gradlew :testLogging --tests *RandomTree
```

This produces output like:

```
                   27        
             ┌─────┴─────┐   
             13          29  
      ┌──────┴──────┐  ┌─┴─┐ 
      8             23 28  30
   ┌──┴──┐       ┌──┴──┐     
   4     11      21    26    
 ┌─┴─┐  ┌┴┐    ┌─┴─┐  ┌┘     
 2   5  9 12   18  22 24     
┌┴┐  └┐ └┐   ┌─┴─┐    └┐     
1 3   6  10  17  19    25    
      └┐    ┌┘   └┐          
       7    15    20         
          ┌─┴─┐              
          14  16             


                 27        
                / \        
               /   \       
              13    29     
             / \   / \     
            /   \ 28  30   
           /     \         
          /       \        
         /         \       
        /           \      
       8             23    
      / \           / \    
     /   \         /   \   
    4     11      /     \  
   / \   / \     21      26
  2   5 9   12  / \     /  
 / \   \ \     18  22  24  
1   3   6 10  / \       \  
         \   17  19      25
          7 /     \        
           15      20      
          / \              
         14  16            


                             27            
                    ┌────────┴────────┐    
                    13                29   
          ┌─────────┴─────────┐    ┌──┴──┐ 
          8                   23   28    30
     ┌────┴────┐         ┌────┴────┐       
     4         11        21        26      
  ┌──┴──┐    ┌─┴─┐    ┌──┴──┐     ┌┘       
  2     5    9   12   18    22    24       
┌─┴─┐   └┐   └┐    ┌──┴──┐        └┐       
1   3    6    10   17    19        25      
         └┐       ┌┘     └┐                
          7       15      20               
               ┌──┴──┐                     
               14    16                    


                      27         
                     / \         
                    /   \        
                   /     \       
                  /       \      
                 13        29    
                / \       / \    
               /   \     /   \   
              /     \   28    30 
             /       \           
            /         \          
           /           \         
          /             \        
         /               \       
        8                 23     
       / \               / \     
      /   \             /   \    
     /     \           /     \   
    4       11        /       \  
   / \     / \       21        26
  2   5   9   12    / \       /  
 / \   \   \       /   \     24  
1   3   6   10    18    22    \  
         \       / \           25
          7     /   \            
               17    19          
              /       \          
             15        20        
            / \                  
           /   \                 
          14    16               

```

There's a [demo program](src/test/java/CollatzTree.java) that produces a tree diagram of all Collatz sequences
(https://en.wikipedia.org/wiki/Collatz_conjecture) of a given length. This demonstrates an option to print trees
in such a way that if there is only a single subtree, it is treated the same regardless of whether it is a left
or right subtree. This produces output like:

```bash
# use gradlew.bat on windows
./gradlew :testLogging --tests *CollatzTree
```

```
                                                       1                                 
                                                       │                                 
                                                       2                                 
                                                       │                                 
                                                       4                                 
                                                       │                                 
                                                       8                                 
                                                       │                                 
                                                       16                                
                                 ┌─────────────────────┴─────────────────────┐           
                                 32                                          5           
                                 │                                           │           
                                 64                                          10          
                      ┌──────────┴──────────┐                      ┌─────────┴─────────┐ 
                     128                    21                     20                  3 
                      │                     │                      │                   │ 
                     256                    42                     40                  6 
           ┌──────────┴──────────┐          │             ┌────────┴────────┐          │ 
          512                    85         84            80                13         12
           │                     │          │             │                 │          │ 
          1024                  170        168           160                26         24
     ┌─────┴─────┐               │          │        ┌────┴────┐            │          │ 
    2048        341             340        336      320        53           52         48
     │           │          ┌────┴────┐     │        │         │        ┌───┴───┐      │ 
    4096        682        680       113   672      640       106      104      17     96
  ┌──┴──┐     ┌──┴──┐       │         │     │     ┌──┴──┐    ┌─┴─┐      │       │      │ 
 8192  1365  1364  227     1360      226   1344  1280  213  212  35    208      34    192
  │     │     │     │    ┌──┴──┐    ┌─┴─┐   │     │     │    │   │    ┌─┴─┐   ┌─┴─┐    │ 
16384  2730  2728  454  2720  453  452  75 2688  2560  426  424  70  416  69  68  11  384
```