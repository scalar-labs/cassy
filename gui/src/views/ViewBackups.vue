<template>
    <div>
        <Backups :backups="backups" :backups_by_snapshot="backups_by_snapshot" :cluster="cluster" @emitSnapshotId="setSnapshotId($event)" />
        <CreateBackup :cluster="cluster" :backups="backups" @updateBackupList="fetchBackups"/>
        <RestoreCluster :cluster="cluster" :snapshot_id="snapshot_id" />
    </div>
</template>

<script>
    import Backups from '../components/Backups'
    import CreateBackup from '../components/CreateBackup'
    import RestoreCluster from '../components/RestoreCluster';

    export default {
      name: 'ViewBackups',
      components: {
        RestoreCluster,
        Backups,
        CreateBackup
      },
      data() {
        return {
          cluster_id: this.$route.params.cluster_id,
          cluster: {},
          backups: {},
          backups_by_snapshot: [[]],
          snapshot_id: ''
        };
      },
      mounted() {
        this.fetchBackups();

        this.$api.get(`clusters/${this.cluster_id}`)
        .then((response) => {
          if (response.status === 200) {
            this.cluster = response.data.entries[0];
          }
        });
      },
      methods: {
        fetchBackups() {
          this.$api.get(`clusters/${this.cluster_id}/backups`)
          .then((response) => {
            if (response.status === 200) {
              this.backups = response.data;
              let count = 0;
              let sortedBackups = Object.assign({}, this.backups);
              sortedBackups.entries.sort((a, b) => (a.snapshot_id > b.snapshot_id) ? 1 : -1);
              let prevId = sortedBackups.entries[0].snapshot_id;
              sortedBackups.entries.forEach(entry => {
                if (entry.snapshot_id === prevId) {
                  this.backups_by_snapshot[count].push(entry);
                } else {
                  count++;
                  this.backups_by_snapshot[count] = [];
                  this.backups_by_snapshot[count].push(entry);
                }
                prevId = entry.snapshot_id;
              });
            }
          });
        },
        setSnapshotId(snapshot_id) {
          this.snapshot_id = snapshot_id
        }
      }
    };
</script>
