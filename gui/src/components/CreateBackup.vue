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
                                <option value="1">Snapshot</option>
                                <option value="2">Incremental</option>
                                <option value="3">Cluster Wide</option>
                            </select>
                        </div>
                        <div class="form-group" v-if="backup_type === '2'">
                            <label for="targetIpsTextArea">Specify nodes (Optional)</label>
                            <textarea class="form-control" id="targetIpsTextArea" rows="3"
                                      placeholder="192.168.0.1, 192.168.0.2, 192.168.0.3"></textarea>
                            <small class="form-text text-muted">Enter the node IP addresses separated by a comma.</small>
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
    },
    data() {
      return {
        backup_type: String,
        target_ips: Array,
      };
    },
    methods: {
      setBackupType() {
        this.backup_type = document.getElementById('backupTypeSelect').value;
      },
      parseTargetIps() {
        return document.getElementById('targetIpsTextArea').value.replace(/\s+/g, '').split(",")
      },
      createBackup() {
        this.parseTargetIps()
        this.$api.post(`clusters/${this.cluster_id}/backups`, {
          backup_type: document.getElementById('backupTypeSelect').value,
          target_ips: this.parseTargetIps()
        }).then((response) => {
          if (response.status === 200) {
            this.$emit('updateBackupList');
          }
        });
      },
    },
  };
</script>
