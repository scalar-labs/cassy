#!/bin/bash
config_file="$1"
cluster_id="$2"

CURRENT_DIR=$(cd $(dirname $0) && cd .. && pwd)
source ${CURRENT_DIR}/util/helper.sh

load_config_file ${config_file}
set_default_parameters
check_config_file

result=$(grpcurl -plaintext -d '{"cluster_id": "'"$cluster_id"'", "backup_type": 1}' localhost:20051 rpc.Cassy.TakeBackup)

status=$(echo $result | jq -r '.status')
if [[ $status != "STARTED" ]]; then
  	send_message_slack "Cluster-wide creation failed."
  	exit 1
fi

end=$(($LIST_BACKUP_LIMIT-1))
is_completed=false

while [[ $is_completed == false ]]; do
	#waiting for backup creation
	sleep $BACKUP_STATUS_CHECK_DURATION
	is_completed=true

	result=$(grpcurl -plaintext -d '{"limit": "'"$LIST_BACKUP_LIMIT"'", "cluster_id": "'"$cluster_id"'"}' localhost:20051  rpc.Cassy.ListBackups)

	for i in $(seq 0 $end); do 
	  status=$(echo $result | jq -r '.entries['"$i"'].status')
	  if [[ $status == "FAILED" ]]; then
	  	send_message_slack "Cluster-wide creation failed."
	  	exit 1
	  else if [[ $status == "STARTED" ]]; then
	  	is_completed=false
	  fi
	  fi
	done
done

#send message to slack
send_message_slack "Cluster-wide successfully created."
