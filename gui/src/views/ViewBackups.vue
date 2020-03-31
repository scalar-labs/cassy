<template>
    <div>
        <Backups :backups="backups" />
    </div>
</template>

<script>
    import Backups from '../components/Backups'

    export default {
      name: 'ViewBackups',
      components: {
        Backups
      },
      props: {
        cluster_id: {
          type: String,
          default: ''
        },
      },
      data() {
        return {
          backups: {},
        };
      },
      mounted() {
        this.cluster_id = this.$route.params.cluster_id;
        this.$api.get(`clusters/${this.cluster_id}/backups`)
        .then((response) => {
          if (response.status === 200) {
            console.log(response.data);
            this.backups = response.data;
          }
        });
      },
    };
</script>
