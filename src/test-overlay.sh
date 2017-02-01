test_home=DijkstraNetworkAssignment/src

for i in `cat machine_list`
	do
		echo 'logging into '${i}
		gnome-terminal -x bash -c "ssh -t ${i} 'cd ${test_home}; java cs455.overlay.node.MessagingNode 129.82.44.156 2000;bash;'" &
	done
