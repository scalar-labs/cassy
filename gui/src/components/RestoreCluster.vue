<template>
    <div class="modal fade" tabindex="-1" id="restoreCluster" aria-labelledby="restoreClusterModal" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Restore From Backup</h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="restoreTypeSelect">Select restore type</label>
                            <select :value=restore_type class="custom-select" id="restoreTypeSelect"
                                    @change="setRestoreType">
                                <option v-for="(t, i) in availableRestoreTypes" :value="t" :key="i">{{ typeName(t) }}
                                </option>
                            </select>
                        </div>
                        <div class="form-group" v-if="restore_type === 2">
                            <label for="targetIpSelect">Specify Target IP (Optional)</label>
                            <select class="custom-select" id="targetIpSelect">
                                <option selected>Choose a node ...</option>
                                <option v-for="(ip, index) in cluster.target_ips" :key="index" @change="setTargetIp">
                                    {{ip}}
                                </option>
                            </select>
                        </div>
                        <small v-if="failed" class="form-text text-danger">Failed to restore backup. Cassy service
                            unavailable.</small>
                        <div class="modal-footer">
                            <button class="btn btn-outline-secondary mx-1" data-toggle="modal"
                                    data-target="#restoreCluster">Cancel
                            </button>
                            <button class="btn btn-primary mx-1" @click="showConfirmationDialogue()">Restore Backup
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
      target_ip: String,
      snapshot_id: String,
      backup_type: Number,
      restore_type: Number,
      changeRestoreType: Function,
    },
    data() {
      return {
        failed: false,
      };
    },
    computed: {
      availableRestoreTypes: function() {
        if (this.backup_type === 1) {
          return [2, 1];
        } else {
          return [2];
        }
      },
    },
    methods: {
      typeName(t) {
        return t === 2 ? 'Node' : 'Cluster';
      },
      showConfirmationDialogue() {
        let targetIps = this.target_ip;
        let data = {
          restore_type: this.restore_type,
        };
        if (this.restore_type === 2 && targetIps) {
          data.target_ips = targetIps;
        }

        $('#restoreCluster').modal('hide');
        $('#confirmRestore').modal('show');
        this.$emit('createRestoreRequestBody', data);
      },
      setRestoreType() {
        let el = document.getElementById('restoreTypeSelect');
        if (el) {
          this.changeRestoreType(parseInt(el.value));
        }
      },
      setTargetIp() {
        let el = document.getElementById('targetIpSelect');
        if (el) {
          this.target_ip = el.value;
        }
      },
    },
  };
</script>
