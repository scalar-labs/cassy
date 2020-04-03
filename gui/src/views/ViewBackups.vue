<template>
    <div>
        <Backups :backups="backups" />
        <CreateBackup :clusters="clusters" :cluster_id="cluster_id" />
    </div>
</template>

<script>
    import Backups from '../components/Backups'
    import CreateBackup from '../components/CreateBackup'

    export default {
      name: 'ViewBackups',
      components: {
        Backups,
        CreateBackup
      },
      data() {
        return {
          cluster_id: this.$route.params.cluster_id,
          clusters: {},
          backups: {},
        };
      },
      mounted() {
        this.$api.get(`clusters/${this.cluster_id}/backups`)
        .then((response) => {
          if (response.status === 200) {
            this.backups = response.data;
          }
        });
        this.$api.get('clusters/')
        .then((response) => {
          if (response.status === 200) {
            this.clusters = response.data;
          }
        });
      },
    };
</script>
