<template>
    <div>
        <Backups :backups="backups" @emitSnapshotId="setSnapshotId($event)" />
        <CreateBackup :clusters="Clusters" :cluster_id="cluster_id" @updateBackupList="fetchBackups"/>
        <RestoreCluster :cluster_id="cluster_id" :snapshot_id="snapshot_id" />
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
          clusters: {},
          backups: {},
          snapshot_id: ''
        };
      },
      mounted() {
        this.fetchBackups();
        this.$api.get('clusters/')
        .then((response) => {
          if (response.status === 200) {
            this.clusters = response.data;
          }
        });
      },
      methods: {
        fetchBackups() {
          this.$api.get(`clusters/${this.cluster_id}/backups`)
          .then((response) => {
            if (response.status === 200) {
              this.backups = response.data;
            }
          });
        },
        setSnapshotId(snapshot_id) {
          console.log(snapshot_id)
          this.snapshot_id = snapshot_id
        }
      }
    };
</script>
