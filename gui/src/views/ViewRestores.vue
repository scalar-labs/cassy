<template>
    <Restores :restores="restores" :cluster="cluster" />
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
        restores: {},
        cluster: {}
      }
    },
    mounted() {
      this.$api.get(`clusters/${this.cluster_id}/data/`)
      .then((response) => {
        if (response.status === 200) {
          this.restores = response.data;
        }
      });
      this.$api.get(`clusters/${this.cluster_id}`)
      .then((response) => {
        if (response.status === 200) {
          this.cluster = response.data.entries[0];
        }
      });
    }
  };
</script>
