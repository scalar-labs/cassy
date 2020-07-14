<template>
    <div class="col">
        <ErrorNotification :error_message="error_message"/>
        <Backups :backups="backups" :backups_by_snapshot="backups_by_snapshot" :cluster="cluster" @emitRestoreParams="setRestoreParams($event)" />
        <CreateBackup :cluster="cluster" :backups="backups" :snapshot_ids="snapshot_ids" @updateBackupList="fetchData"/>
        <RestoreCluster
                :cluster="cluster"
                :snapshot_id="snapshot_id"
                :backup_type="backup_type"
                :target_ip="target_ip"
                :changeRestoreType=changeRestoreType
                @createRestoreRequestBody="createRestoreRequestBody($event)" />
        <ConfirmRestore :cluster_id="cluster_id" :snapshot_id="snapshot_id" :data="restore_request_body"/>
    </div>
</template>

<script>
    import Backups from '../components/Backups'
    import CreateBackup from '../components/CreateBackup'
    import RestoreCluster from '../components/RestoreCluster';
    import ConfirmRestore from '../components/ConfirmRestore';
    import ErrorNotification from '../components/ErrorNotification';

    export default {
      name: 'ViewBackups',
      props: {
        error_message: String,
      },
      components: {
        ErrorNotification,
        RestoreCluster,
        Backups,
        CreateBackup,
        ConfirmRestore
      },
      data() {
        return {
          cluster_id: this.$route.params.cluster_id,
          cluster: {},
          backups: {},
          backups_by_snapshot: [[]],
          snapshot_ids: Set,
          snapshot_id: '',
          backup_type: 0,
          target_ip: '',
          restore_request_body: {},
          autoRefreshTimer: ''
        };
      },
      mounted() {
        this.fetchData();
        this.autoRefreshTimer = setInterval(this.fetchData, 10000);

      },
      beforeDestroy() {
        clearInterval(this.autoRefreshTimer);
      },
      methods: {
        changeRestoreType(type) {
          this.restore_type = type;
        },
        fetchData() {
          this.$api.get(`clusters/${this.cluster_id}`)
          .then((response) => {
            if (response.status === 200) {
              this.cluster = response.data.entries[0];
            }
          }).catch(error => {
            if (error.code === 503) {
              let message = "Failed to fetch cluster \"" + this.cluster_id + "\".";
              this.$emit('showError', message);
            }
          });
          this.$api.get(`clusters/${this.cluster_id}/backups`)
          .then((response) => {
            if (response.status === 200) {
              this.backups = response.data;

              // this code attempts to convert the backups object into a 2d array, with each index representing
              // a unique snapshot_id
              if (this.backups.entries) {
                let count = 0;
                let sortedBackups = Object.assign({}, this.backups);
                sortedBackups.entries.sort((a, b) => (a.snapshot_id > b.snapshot_id) ? 1 : -1);
                let prevId = sortedBackups.entries[0].snapshot_id;
                let snapshot_ids = [];

                this.backups_by_snapshot = [[]];
                sortedBackups.entries.forEach(entry => {
                  snapshot_ids.push(entry.snapshot_id); // this will be used to make a Set of unique snapshot_ids
                  if (entry.snapshot_id === prevId) {
                    this.backups_by_snapshot[count].push(entry); // add entries to this index until the snapshot_id changes
                  } else {
                    count++;
                    this.backups_by_snapshot[count] = []; // initialize this index
                    this.backups_by_snapshot[count].push(entry);
                  }
                  prevId = entry.snapshot_id;
                });
                this.snapshot_ids = new Set(snapshot_ids); // this removes any duplicate values

                // this loop will remove older incremental backups
                for (let i = 0; i < this.backups_by_snapshot.length; i++) {
                  let count = 0;
                  let snapshot = this.backups_by_snapshot[i];
                  for (let j = 0; j < snapshot.length; j++) {
                    if (snapshot[j].backup_type === 3) {
                      if (count > 0 && snapshot[j].target_ip === snapshot[j-1].target_ip) {
                        this.backups_by_snapshot[i].splice(j, 1);
                        j--;
                      }
                      count++;
                    }
                  }
                }
              }
              this.backups_by_snapshot.sort((a, b) => (a[0].created_at > b[0].created_at ? -1 : 1));
            }
          }).catch(error => {
            if (error.code === 503) {
              let message = "Failed to fetch backups for cluster \"" + this.cluster_id + "\".";
              this.$emit('showError', message);
            }
          });
        },
        setRestoreParams(event) {
          this.snapshot_id = event.snapshot_id;
          this.backup_type = event.backup_type;
          this.target_ip = event.target_ip;
        },
        createRestoreRequestBody(event) {
          this.restore_request_body = event;
        }
      }
    };
</script>
