import { ImageSourcePropType } from 'react-native';

const JABEA_IMAGES: Record<string, ImageSourcePropType> = {
  aquaryx: require('../assets/jabeas/aquaryx.png'),
  cindrel: require('../assets/jabeas/cindrel.png'),
  flamara: require('../assets/jabeas/flamara.png'),
  ignarok: require('../assets/jabeas/ignarok.png'),
  marvyn: require('../assets/jabeas/marvyn.png'),
  nerulon: require('../assets/jabeas/nerulon.png'),
  pyronox: require('../assets/jabeas/pyronox.png'),
  tiderra: require('../assets/jabeas/tiderra.png'),
};

const PLACEHOLDER_IMAGE = require('../assets/jabeas/placeholder.png');

export function getJaBeaImage(name?: string | null): ImageSourcePropType {
  if (!name) {
    return PLACEHOLDER_IMAGE;
  }

  return JABEA_IMAGES[normalizeJaBeaName(name)] ?? PLACEHOLDER_IMAGE;
}

function normalizeJaBeaName(name: string) {
  return name
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .trim()
    .toLowerCase();
}
