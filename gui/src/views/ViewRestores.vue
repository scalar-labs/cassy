<template>
    <Restores :restores="restores" />
</template>

<script>
  import Restores from '../components/Restores';

  export default {
    name: 'ViewRestores',
    components: {
      Restores
    },
    data() {
      return {
        cluster_id: this.$route.params.cluster_id,
        snapshot_id: this.$route.params.snapshot_id,
        restores: {}
      }
    },
    mounted() {
      this.$api.get(`clusters/${this.cluster_id}/data/${this.snapshot_id}`)
      .then((response) => {
        if (response.status === 200) {
          this.restores = response.data;
        }
      });
    }
  };
</script>
