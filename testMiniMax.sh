!#/bin/bash

clear
player1=0
player2=0
rm Part3_massoudi_racah.txt
echo `date` >> Part3_massoudi_racah.txt
rm outputAlphaBeta_connect5.txt
echo -n "Playing against RandomAI"
echo -e "\nPlaying against RandomAI" >> Part3_massoudi_racah.txt
for i in {1..5}
	do java Main -p1 alphabeta_connect5 -p2 RandomAI -text -seed $i >> outputAlphaBeta_connect5.txt
	echo -n "."
	echo -ne "\n seed: ">> Part3_massoudi_racah.txt ; echo $i >> Part3_massoudi_racah.txt
	tail -8 outputAlphaBeta_connect5.txt >> Part3_massoudi_racah.txt
	# lastLine=tail -8  outputMiniMaxAI.txt
	# if(lastLine=="Player 1 won")
	# then
	# 	player1+=1
	# else
	# 	player2+=1
	# fi
	# echo -n "player1: " ; echo -n $player1
	# echo -n "player2: " ; echo -n $player2

done
echo "done"

echo -n "Playing against StupidAI"
echo -e "\nPlaying against StupidAI" >> Part3_massoudi_racah.txt
for i in {1..5}
	do java Main -p1 alphabeta_connect5 -p2 StupidAI -text -seed $i >> outputAlphaBeta_connect5.txt
	echo -n "."
	echo -ne "\n seed: ">> Part3_massoudi_racah.txt ; echo $i >> Part3_massoudi_racah.txt
	tail -8 outputAlphaBeta_connect5.txt >> Part3_massoudi_racah.txt
done
echo "done"

echo -n "Playing against MonteCarloAI"
echo -e "\nPlaying against MonteCarloAI" >> Part3_massoudi_racah.txt
for i in {1..10}
	do java Main -p1 alphabeta_connect5 -p2 MonteCarloAI -text -seed $i >> outputAlphaBeta_connect5.txt
	echo -n "."
	echo -ne "\n seed: ">> Part3_massoudi_racah.txt ; echo $i >> Part3_massoudi_racah.txt
	tail -8 outputAlphaBeta_connect5.txt >> Part3_massoudi_racah.txt
done
echo "done"

exit 0

