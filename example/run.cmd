SET jar=target/my-node2vec-1.0-jar-with-dependencies.jar
SET class_path=Main

SET graph=example/graph1.txt
SET weighting_exponent=0.75
SET learning_rate=0.1
SET n_nev_samples=5
SET n_walks=50
SET walk_length=5
SET return_param=1.0
SET in_out_param=1.0
SET output=example/output
SET embedding_dimension=5

java -cp %jar% %class_path% -g %graph% -we %weighting_exponent% -lr %learning_rate% -nn %n_nev_samples% -nw %n_walks% -wl %walk_length% -p %return_param% -q %in_out_param% -o %output% -d %embedding_dimension%
