<template>
    <div class="modal fade" tabindex="-1" id="registerBackup" aria-labelledby="registerBackupModal" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Start a new backup</h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="backupTypeSelect">Select a backup type</label>
                            <select class="custom-select" id="backupTypeSelect" @change="setBackupType">
                                <option selected value="2">Snapshot</option>
                                <option value="1">Cluster-Wide</option>
                                <option value="3">Incremental</option>
                            </select>
                        </div>
                        <div class="form-group" v-if="backup_type === '2'">
                            <label for="targetIpSelect">Specify Target IP (Optional)</label>
                            <select class="custom-select" id="targetIpSelect">
                                <option selected>Choose a node ...</option>
                                <option v-for="(ip, index) in cluster.target_ips" :key="index" @change="setTargetIp">{{ip}}</option>
                            </select>
                        </div>
                        <div class="form-group" v-if="backup_type === '3'">
                            <label for="snapshotIdSelect">Choose a Snapshot ID</label>
                            <select class="custom-select" id="snapshotIdSelect" @change="setSnapshotId">
                                <option selected>Snapshot ID</option>
                                <option v-for="(id, index) in snapshot_ids" :key="index">{{id}}</option>
                            </select>
                        </div>
                        <small v-if="failed" class="form-text text-danger">Failed to create backup. Cassy service unavailable.</small>
                        <div class="modal-footer">
                            <button class="btn btn-outline-secondary mx-1"
                                    data-target="#registerBackup"
                            >Cancel
                            </button>
                            <button class="btn btn-primary mx-1"
                                    data-target="#registerBackup"
                                    @click="createBackup"
                            >Create Backup
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
  import $ from 'jquery';

  export default {
    props: {
      cluster: {},
      backups: {},
      snapshot_ids: {}
    },
    data() {
      return {
        backup_type: '2',
        snapshot_id: String,
        target_ip: String,
        failed: false,
      };
    },
    methods: {
      setBackupType() {
        let el = document.getElementById('backupTypeSelect');
        if (!el) {
          return null;
        }
        this.backup_type = el.value;
      },
      setSnapshotId() {
        let el = document.getElementById('snapshotIdSelect');
        if (el) {
          this.snapshot_id = el.value;
        }
      },
      setTargetIp() {
        let el = document.getElementById('targetIpSelect');
        if (el) {
          this.target_ip = el.value;
        }
      },
      createBackup() {
        let targetIps = this.target_ip;
        let data = {
          backup_type: this.backup_type,
        };
        if (this.backup_type === '2' && targetIps) {
          data.target_ips = targetIps;
        }
        if (this.backup_type === '3' && this.snapshot_id) {
          data.snapshot_id = this.snapshot_id
        }

        this.$api.post(`clusters/${this.cluster.cluster_id}/backups`, data).then((response) => {
          if (response.status === 200) {
            $('#registerBackup').modal('hide');
            this.failed = false;
            this.$emit('updateBackupList');
          }
        })
        .catch(error => {
          console.log(error.response);
            this.failed = true;
        });
      },
    },
  };
</script>
