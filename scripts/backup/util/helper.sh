#!/bin/bash

load_config_file() {
  config="$1"
  if [[ -f ${config} ]]; then
    source ${config}
  else
    echo "cannot access ${config}: No such file"
    exit 1
  fi
}

check_snapshot_config_file() {
  if [[ -z ${BACKUP_STATUS_CHECK_DURATION} ]] ||
     [[ -z ${SLACK_CHANNEL} ]] ||
     [[ -z ${SLACK_TOKEN} ]]; then
     echo "backup create: the config file seems to be incorrectly specified"
     exit 1
  fi
}

set_default_parameters(){
  if [[ -z "${LIST_BACKUP_LIMIT}" ]]; then
    LIST_BACKUP_LIMIT=3;
  fi
}

send_message_slack() {
  curl -X POST -d token=${SLACK_TOKEN} -d channel=${SLACK_CHANNEL} -d text="$1" https://slack.com/api/chat.postMessage
}

check_cassy_respose(){
  response=$1
  status_expected=$2
  end=$(($LIST_BACKUP_LIMIT-1))
  for i in $(seq 0 $end); do 
    status=$(echo $response | jq -r '.entries['"$i"'].status')
    if [[ $status != $status_expected ]]; then
      send_message_slack "${backup_name} creation failed"
      exit 1
    fi
  done
}

