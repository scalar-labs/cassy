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
                                <option selected>Backup Type</option>
                                <option value="1">Cluster-Wide</option>
                                <option value="2">Snapshot</option>
                                <option value="3">Incremental</option>
                            </select>
                        </div>
                        <div class="form-group" v-if="backup_type === '2'">
                            <label for="targetIpsTextArea">Specify nodes (Optional)</label>
                            <textarea class="form-control" id="targetIpsTextArea" rows="3"
                                      placeholder="192.168.0.1, 192.168.0.2, 192.168.0.3"></textarea>
                            <small class="form-text text-muted">Enter the node IP addresses separated by a
                                comma.</small>
                        </div>
                        <div class="form-group" v-if="backup_type === '3'">
                            <label for="snapshotIdSelect">Choose a Snapshot ID</label>
                            <select class="custom-select" id="snapshotIdSelect" @change="setSnapshotId">
                                <option selected>Snapshot ID</option>
                                <option v-for="(b, index) in backups.entries" :key="index">{{b.snapshot_id}}</option>
                            </select>
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-primary"
                                    data-toggle="modal"
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
  export default {
    props: {
      cluster_id: String,
      backups: {},
    },
    data() {
      return {
        backup_type: String,
        snapshot_id: String,
        target_ips: Array,
      };
    },
    methods: {
      setBackupType() {
        let element = document.getElementById('backupTypeSelect');
        if (!element) {
          return null;
        }
        this.backup_type = element.value;
      },
      setSnapshotId() {
        let element = document.getElementById('snapshotIdSelect');
        if (!element) {
          return null;
        }
        this.snapshot_id = element.value;
      },
      parseTargetIps() {
        let element = document.getElementById('targetIpsTextArea');
        if (!element) {
          return null;
        }
        let targetIps = element.value;
        if (targetIps) {
          targetIps = targetIps.replace(/\s+/g, '').split(',');
        }
        return targetIps;
      },
      createBackup() {
        let targetIps = this.parseTargetIps();
        let data = {
          backup_type: this.backup_type,
        };
        if (this.backup_type === '2' && targetIps) {
          data.target_ips = targetIps;
        }
        if (this.backup_type === '3' && this.snapshot_id) {
          data.snapshot_id = this.snapshot_id
        }

        this.$api.post(`clusters/${this.cluster_id}/backups`, data).then((response) => {
          if (response.status === 200) {
            this.$emit('updateBackupList');
          }
        });
      },
    },
  };
</script>
